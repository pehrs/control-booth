package com.pehrs.controlbooth.config;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableAspectJAutoProxy
@AllArgsConstructor
public class Oauth2SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http

        .authorizeHttpRequests(auth ->
            auth
                .requestMatchers("/",
                    "/api/user/logout",
                    "/api/user/profile",
                    "/api/about/**",
                    "/webjars/**",
                    "/images/**",
                    "/css/**",
                    "/js/**",
                    "/actuator/**",
                    "/setup/**"
                )
                .permitAll()
                .anyRequest().authenticated()
        )
        .oauth2Login(Customizer.withDefaults())  // Enables OAuth2 login
        .oauth2Client(Customizer.withDefaults()) // Enables OAuth2 client
        .csrf(csrf -> csrf.disable())  // Disable CSRF for APIs
        .cors(cors -> cors.configurationSource(corsConfigurationSource())); // Enable CORS

    return http.build();
  }


  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.setAllowedOrigins(List.of(
        // FIXME: This needs to be in the application.yml config
        "http://auth.nsa2.com:9000",  // Keycloak

//        "http://gateway.nsa2.com:8080",
//        "http://gateway.nsa2.com:3030"
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
