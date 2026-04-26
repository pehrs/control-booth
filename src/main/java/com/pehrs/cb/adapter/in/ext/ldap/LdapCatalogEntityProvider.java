package com.pehrs.cb.adapter.in.ext.ldap;

import com.pehrs.cb.config.LdapConfig;
import com.pehrs.cb.port.in.CatalogApi;
import com.pehrs.cb.port.in.spi.CatalogProvider;
import com.pehrs.cb.core.domain.Group;
import com.pehrs.cb.core.domain.User;
import com.pehrs.cb.core.domain.User.UserBuilder;
import jakarta.validation.constraints.Size;
import java.util.Base64;
import java.util.Collections;
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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "catalog.ldap-provider.enabled", havingValue = "true")
@Slf4j
@AllArgsConstructor
public class LdapCatalogEntityProvider implements CatalogProvider {

  LdapConfig ldapConfig;

  @Override
  public String getId() {
    return "ldap";
  }

  @Override
  public void refresh(CatalogApi api) throws Exception {
    scan4UsersAndGroups(api);
    scan4GroupMembers(api);
  }

  // ── LDAP helpers ─────────────────────────────────────────────────────────

  private DirContext getContext() throws NamingException {
    Hashtable<String, String> env = ldapConfig.getLdapAdminEnv();
    return new InitialDirContext(env);
  }

  private NamingEnumeration<SearchResult> search(DirContext ctx, String objectClass)
      throws NamingException {
    SearchControls controls = new SearchControls();
    controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    controls.setCountLimit(ldapConfig.getLdapSearchLimit());
    return ctx.search(ldapConfig.getSearchRoot(), String.format("(objectClass=%s)", objectClass), controls);
  }

  private List<String> getAttributes(SearchResult sr, String attributeName) throws NamingException {
    Attribute attr = sr.getAttributes().get(attributeName);
    NamingEnumeration<String> values = (NamingEnumeration<String>) attr.getAll();
    return Collections.list(values);
  }

  private List<String> getObjectClasses(SearchResult sr) throws NamingException {
    return getAttributes(sr, "objectClass");
  }

  private @Size(max = 255) String getDescription(SearchResult sr) {
    Attribute attr = sr.getAttributes().get("description");
    return attr == null ? "" : attr.toString();
  }

  private @Size(max = 255) String getUserDisplayName(SearchResult sr) throws NamingException {
    Attributes attributes = sr.getAttributes();
    Attribute attr = attributes.get("displayName");
    if (attr != null) return attr.toString().replace("displayName: ", "");
    attr = attributes.get("cn");
    if (attr != null) return attr.toString().replace("cn: ", "");
    attr = attributes.get("uid");
    if (attr != null) return attr.toString().replace("uid: ", "");
    throw new NamingException("Could not extract display name for LDAP entry");
  }

  private static String getUid(SearchResult sr) {
    return sr.getAttributes().get("uid").toString().replace("uid: ", "");
  }

  private static String getCn(SearchResult sr) {
    return sr.getAttributes().get("cn").toString().replace("cn: ", "");
  }

  // ── Scan phases ──────────────────────────────────────────────────────────

  private void scan4UsersAndGroups(CatalogApi api) throws NamingException {
    DirContext ctx = getContext();
    try {
      NamingEnumeration<SearchResult> results = search(ctx, "*");
      while (results.hasMoreElements()) {
        SearchResult sr = results.next();
        List<String> objectClasses = getObjectClasses(sr);

        if (objectClasses.contains("posixGroup")) {
          String dn = sr.getNameInNamespace();
          String groupName = getCn(sr);
          log.info("Upserting group {}", groupName);

          api.upsertGroup(Group.builder()
              .name(groupName)
              .displayName(groupName)
              .entityType("LDAP_GROUP")
              .description(getDescription(sr))
              .annotations(Map.of("control-booth/ldap-dn", dn))
              .build());

        } else if (objectClasses.contains("inetOrgPerson")) {
          String uid = getUid(sr);
          String displayName = getUserDisplayName(sr);
          String dn = sr.getNameInNamespace();
          log.info("Upserting user {}", uid);

          UserBuilder user = User.builder()
              .email(sr.getAttributes().get("mail").toString().replace("mail: ", ""))
              .name(uid)
              .entityType("LDAP_USER")
              .description(getDescription(sr))
              .annotations(Map.of("control-booth/ldap-dn", dn))
              .displayName(displayName);

          Attribute jpegPhoto = sr.getAttributes().get("jpegPhoto");
          if (jpegPhoto != null) {
            byte[] jpegBytes = (byte[]) jpegPhoto.get();
            user.picture(new String(Base64.getEncoder().encode(jpegBytes)));
          }
          api.upsertUser(user.build());
        }
      }
    } finally {
      ctx.close();
    }
  }

  private void scan4GroupMembers(CatalogApi api) throws NamingException {
    DirContext ctx = getContext();
    try {
      NamingEnumeration<SearchResult> results = search(ctx, "posixGroup");
      while (results.hasMoreElements()) {
        SearchResult sr = results.next();
        if (!getObjectClasses(sr).contains("posixGroup")) continue;

        String groupName = getCn(sr);
        api.findGroup(groupName).ifPresent(groupDTO -> {
          Attribute members = sr.getAttributes().get("memberUid");
          if (members == null) return;
          try {
            NamingEnumeration<String> memberEnumeration =
                (NamingEnumeration<String>) members.getAll();
            while (memberEnumeration.hasMoreElements()) {
              String memberUid = memberEnumeration.next();
              api.findUser(memberUid).ifPresent(userDTO -> {
                Set<String> groups = new HashSet<>(userDTO.getGroups());
                groups.add(groupDTO.getName());
                userDTO.setGroups(groups);
                api.updateUser(userDTO.getId(), userDTO);
              });
            }
          } catch (NamingException e) {
            throw new RuntimeException(e);
          }
        });
      }
    } finally {
      ctx.close();
    }
  }
}
