package com.pehrs.cb.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

  private LdapAuthProvider authenticationProvider;


  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // Define URL-based authorization rules
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/login", "/login/**", "/css/**").permitAll()
            .anyRequest().authenticated()
        )
        // Configure form login with custom page
        .formLogin(form -> form
            .loginPage("/login/index.html")
            .loginProcessingUrl("/perform_login")
            .defaultSuccessUrl("/html/index", true)
            .failureUrl("/login/index.html?error=true")
            .permitAll()
        )
        // Register custom authentication provider
        .authenticationProvider(authenticationProvider)
        // Disable CSRF if using stateless APIs or handling tokens manually
        .csrf(AbstractHttpConfigurer::disable);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
