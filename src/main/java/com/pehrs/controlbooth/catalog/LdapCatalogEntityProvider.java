package com.pehrs.controlbooth.catalog;

import com.pehrs.controlbooth.model.GroupDTO;
import com.pehrs.controlbooth.model.UserDTO;
import com.pehrs.controlbooth.model.UserDTO.UserDTOBuilder;
import com.pehrs.controlbooth.service.GroupService;
import com.pehrs.controlbooth.service.UserService;
import jakarta.validation.constraints.Size;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LdapCatalogEntityProvider {

  private final UserService userService;
  private final GroupService groupService;

  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

  @Value("${catalog.ldap-provider.hostname:localhost}")
  private String ldapHostname;

  @Value("${catalog.ldap-provider.port:389}")
  private int ldapPort;

  @Value("${catalog.ldap-provider.admin.cn:cn=admin,dc=control-booth,dc=org}")
  private String adminDn;

  @Value("${catalog.ldap-provider.admin.password:admin}")
  private String adminPassword;

  @Value("${catalog.ldap-provider.search.root:dc=control-booth,dc=org}")
  private String searchRoot;

  @Value("${catalog.ldap-provider.search.limit:10000}")
  private int ldapSearchLimit;

  public LdapCatalogEntityProvider(GroupService groupService, UserService userService) {
    this.userService = userService;
    this.groupService = groupService;
  }

  private DirContext getContext() throws NamingException {
    Hashtable<String, String> env = new Hashtable<>();
    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
    env.put(Context.PROVIDER_URL, String.format("ldap://%s:%d", ldapHostname, ldapPort));
    env.put(Context.SECURITY_AUTHENTICATION, "simple");
    env.put(Context.SECURITY_PRINCIPAL, adminDn);
    env.put(Context.SECURITY_CREDENTIALS, adminPassword);

    return new InitialDirContext(env);
  }

  @Scheduled(
      fixedRateString = "${tg.entity.scan.rate:5m}",
      initialDelayString = "${tg.entity.scan.initial-delay:3s}"
  )
  public void scan() throws IOException, NamingException {
    log.info("The time is now {}", dateFormat.format(new Date()));

    // Scan for Groups and Users
    scan4UsersAndGroups();
    scan4GroupMembers();
  }


  private List<String> getAttributes(SearchResult sr, String attributeName) throws NamingException {
    javax.naming.directory.Attribute members = sr.getAttributes().get(attributeName);
    NamingEnumeration<String> memberEnumeration = (NamingEnumeration<String>) members.getAll();
    return Collections.list(memberEnumeration).stream()
        .toList();
  }

  private List<String> getObjectClasses(SearchResult sr) throws NamingException {
    return getAttributes(sr, "objectClass");
  }

  private List<String> getMemberUids(SearchResult sr) throws NamingException {
    return getAttributes(sr, "memberUid");
  }

  private @Size(max = 255) String getDescription(SearchResult sr) {
    Attributes attributes = sr.getAttributes();
    Attribute attribute = attributes.get("description");
    if (attribute == null) {
      return "";
    }
    return attribute.toString();
  }


  private @Size(max = 255) String getUserDisplayName(SearchResult sr) throws NamingException {
    Attributes attributes = sr.getAttributes();
    Attribute attribute = attributes.get("displayName");
    if (attribute != null) {
      return attribute.toString().replace("displayName: ", "");
    }
    attribute = attributes.get("cn");
    if (attribute != null) {
      return attribute.toString().replace("cn: ", "");
    }
    attribute = attributes.get("uid");
    if (attribute != null) {
      return attribute.toString().replace("uid: ", "");
    }
    throw new NamingException("Could not extract display name for LDAP entry");
  }

  private static String getUid(SearchResult sr) {
    String uid = sr.getAttributes().get("uid")
        .toString()
        .replace("uid: ", "");
    return uid;
  }

  private static String getCn(SearchResult sr) {
    return sr.getAttributes().get("cn")
        .toString()
        .replace("cn: ", "");
  }

  private NamingEnumeration<SearchResult> search(DirContext ctx, String objectClass)
      throws NamingException {
    SearchControls searchControls = new SearchControls();
    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    searchControls.setCountLimit(ldapSearchLimit);
    return ctx.search(searchRoot, String.format("(objectClass=%s)", objectClass), searchControls);
  }

  private void scan4UsersAndGroups() throws IOException, NamingException {

    DirContext ctx = getContext();
    try {
      NamingEnumeration<SearchResult> namingEnumeration = search(ctx, "*");
      while (namingEnumeration.hasMoreElements()) {
        SearchResult sr = namingEnumeration.next();

        List<String> objectClasses = getObjectClasses(sr);
        if (objectClasses.contains("posixGroup")) {
          log.info("ADD GROUP {}", sr);
          String dn = sr.getNameInNamespace();
          String groupName = getCn(sr);

          GroupDTO group = GroupDTO.builder()
              .name(groupName)
              .displayName(groupName)
              .entityType("LDAP_GROUP")
              .description(getDescription(sr))
              .annotations(Map.of(
                  "control-booth/ldap-dn", dn
              ))
              .build();

          // Update or create group
          groupService.upsert(group);

        } else if (objectClasses.contains("inetOrgPerson")) {

          log.info("ADD USER {}", sr);
          String uid = getUid(sr);
          String displayName = getUserDisplayName(sr);
          String dn = sr.getNameInNamespace();
          UserDTOBuilder user = UserDTO.builder()
              .email(sr.getAttributes().get("mail").toString().replace("mail: ", ""))
              .name(uid)
              .entityType("LDAP_USER")
              .description(getDescription(sr))
              .annotations(Map.of(
                  "control-booth/ldap-dn", dn
              ))
              .displayName(displayName);
          Attribute jpegPhoto = sr.getAttributes().get("jpegPhoto");
          if (jpegPhoto != null) {
            byte[] jpegBytes = (byte[]) jpegPhoto.get();
            byte[] encoded = Base64.getEncoder().encode(jpegBytes);
            String b64 = new String(encoded);
            user.picture(b64);
          }
          userService.upsert(user.build());
        }
      }

    } finally {
      ctx.close();
    }

  }


  private void scan4GroupMembers() throws NamingException {

    DirContext ctx = getContext();
    try {
      NamingEnumeration<SearchResult> namingEnumeration = search(ctx, "posixGroup");
      while (namingEnumeration.hasMoreElements()) {
        SearchResult sr = namingEnumeration.next();
        List<String> objectClasses = getObjectClasses(sr);
        if (objectClasses.contains("posixGroup")) {
          log.info("ADD GROUP {}", sr);
          String dn = sr.getNameInNamespace();
          String groupName = getCn(sr);
          // Get or create group
          groupService.getByName(groupName)
              .ifPresent(groupDTO -> {
                Attribute members = sr.getAttributes().get("memberUid");
                if (members != null) {
                  try {
                    NamingEnumeration<String> memberEnumeration = (NamingEnumeration<String>) members.getAll();
                    while (memberEnumeration.hasMoreElements()) {
                      try {
                        String memberUid = memberEnumeration.next();
                        System.out.println("  member: " + memberUid);

                        userService.findUser(memberUid)
                            .ifPresent(userDTO -> {
                              // Add the group to the user
                              Set<String> newGroups = new HashSet<>(userDTO.getGroups());
                              newGroups.add(groupDTO.getName());
                              userDTO.setGroups(newGroups);
                              userService.update(userDTO.getId(), userDTO);
                            });

                      } catch (NamingException e) {
                        throw new RuntimeException(e);
                      }
                    }
                  } catch (NamingException e) {
                    throw new RuntimeException(e);
                  }
                }
              });
        }
      }
    } finally {
      ctx.close();
    }
  }

}
