package com.pehrs.cb.config;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

//@Configuration
//@EnableAspectJAutoProxy
@AllArgsConstructor
public class Oauth2SecurityConfig {

  private LdapAuthProvider lynxAuthProvider;

  // @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth ->
            auth
                .requestMatchers("/",
                    "/api/user/logout",
                    "/login/**",
                    "/api/user/profile",
                    "/api/about/**",
                    "/webjars/**",
                    "/images/**",
                    "/css/**",
                    "/js/**",
                    "/actuator/**",
                    "/login*",
                    "/login/**",
                    "/setup/**"
                )
                .permitAll()
                .anyRequest().authenticated()
        )
        .oauth2Login(Customizer.withDefaults())  // Enables OAuth2 login
        .oauth2Client(Customizer.withDefaults()) // Enables OAuth2 client
        .csrf(AbstractHttpConfigurer::disable)  // Disable CSRF for APIs
        .cors(cors -> cors.configurationSource(corsConfigurationSource())); // Enable CORSs
    ;

    return http.build();
  }


  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.setAllowedOrigins(List.of(
        // FIXME: This needs to be in the application.yml config
        "http://auth.nsa2.com:9000",  // Keycloak

        // FIXME: This needs to be in the application.yml config
        "http://control-booth.org:8080",
        "http://control-booth.org:3030"
    ));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }

}
