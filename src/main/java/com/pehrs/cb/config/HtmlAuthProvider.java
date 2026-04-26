package com.pehrs.cb.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;

import java.util.List;

@Configuration
@Slf4j
@AllArgsConstructor
public class HtmlAuthProvider implements AuthenticationProvider {

    HtmlConfig htmlConfig;

    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String principalName = authentication.getName();
        String credentials = "" + authentication.getCredentials();

        if (!principalName.equals(htmlConfig.clientId) || !credentials.equals(htmlConfig.clientSecret)) {
            log.warn("Failed login for {}", principalName);
            return null;
        }

        log.info("name={}", authentication.getName());
        log.info("principal={}", authentication.getPrincipal());
        log.info("credentials={}", authentication.getCredentials());
        log.info("details={}", authentication.getDetails());

        OidcIdToken oidcIdToken = OidcIdToken
                .withTokenValue("token")
                .claim("testClaim", "claim")
                .subject("cn=xx,a=f")
                .build();
        OidcUserInfo userInfo = OidcUserInfo.builder()
                .email("m@m.com")
                .name("name")
                .familyName("familyName")
                .build();
        List<GrantedAuthority> authorities = List.of(
                new OidcUserAuthority(
                        oidcIdToken,
                        userInfo
                )
        );
//        List<GrantedAuthority> authorities = List.of(
//            new SimpleGrantedAuthority("ROLE_USER")
//        );
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
}
