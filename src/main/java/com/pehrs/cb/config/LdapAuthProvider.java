package com.pehrs.cb.config;

import java.util.Hashtable;
import java.util.Objects;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;

import java.util.List;

@Configuration
@Slf4j
@AllArgsConstructor
public class LdapAuthProvider implements AuthenticationProvider {

  LdapConfig ldapConfig;

  @Override
  public @Nullable Authentication authenticate(Authentication authentication)
      throws AuthenticationException {

    String principalName = authentication.getName();
    String credentials = "" + authentication.getCredentials();

    SearchResult result = findUserDn(principalName);
    String userDn = Objects.requireNonNull(result).getNameInNamespace();
    if (!bindAsUser(userDn, credentials)) {
      log.warn("Login failed for {}", principalName);
      return null;
    }

    String mail = result.getAttributes().get("mail").toString();
    String displayName = result.getAttributes().get("displayName").toString();
    String givenName = result.getAttributes().get("givenName").toString();
    String sn = result.getAttributes().get("sn").toString();

    log.info("name={}", authentication.getName());
    log.info("dn={}", userDn);
    log.info("details={}", authentication.getDetails());

    OidcIdToken oidcIdToken = OidcIdToken
        .withTokenValue("token") // FIXME Generate a token here
        .claim("sessionDetails", authentication.getDetails())
        .subject(userDn)
        .build();
    OidcUserInfo userInfo = OidcUserInfo.builder()
        .email(mail)
        .name(displayName)
        .givenName(givenName)
        .familyName(sn)
        .build();
//        List<GrantedAuthority> authorities = List.of(
//                new OidcUserAuthority(
//                        oidcIdToken,
//                        userInfo
//                )
//        );
    List<GrantedAuthority> authorities = List.of(
        new SimpleGrantedAuthority("ROLE_USER")
    );
    DefaultOidcUser principal = new DefaultOidcUser(
        authorities,
        oidcIdToken,
        userInfo
    );

    return new UsernamePasswordAuthenticationToken(principal, null, authorities);
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }


  private SearchResult findUserDn(String username) {
    Hashtable<String, String> env = ldapConfig.getLdapAdminEnv();
    try {
      DirContext ctx = new InitialDirContext(env);
      try {
        SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        controls.setReturningAttributes(new String[]{
            "mail",
            "displayName",
            "givenName",
            "sn",
        });
        controls.setCountLimit(1);
        NamingEnumeration<SearchResult> results = ctx.search(
            ldapConfig.getSearchRoot(),
            String.format("(uid=%s)", escapeLdapFilter(username)),
            controls
        );
        if (results.hasMoreElements()) {
          return results.next();
        }
        return null;
      } finally {
        ctx.close();
      }
    } catch (NamingException e) {
      log.error("LDAP search failed for user {}: {}", username, e.getMessage());
      throw new BadCredentialsException("LDAP lookup failed", e);
    }
  }

  /**
   * Attempts to bind to LDAP as the given DN with the given password. Returns true if the bind
   * succeeds (credentials are valid).
   */
  private boolean bindAsUser(String userDn, String password) {
    Hashtable<String, String> env = ldapConfig.getLdapEnv(userDn, password);
    try {
      DirContext ctx = new InitialDirContext(env);
      ctx.close();
      return true;
    } catch (NamingException e) {
      return false;
    }
  }

  /**
   * Escapes special characters in an LDAP search filter value per RFC 4515.
   */
  private static String escapeLdapFilter(String value) {
    return value
        .replace("\\", "\\5c")
        .replace("*", "\\2a")
        .replace("(", "\\28")
        .replace(")", "\\29")
        .replace("\0", "\\00");
  }

}
