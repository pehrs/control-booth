package com.pehrs.cb.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {


  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // allow all for now...
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/**").permitAll()
            .anyRequest().authenticated()
        )
        // Disable CSRF if using stateless APIs or handling tokens manually
        .csrf(AbstractHttpConfigurer::disable);

    return http.build();
  }

}
