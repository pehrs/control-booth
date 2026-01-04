package com.pehrs.controlbooth.controller;

import com.pehrs.controlbooth.controller.UserRestController.UserProfile.UserProfileBuilder;
import com.pehrs.controlbooth.model.UserDTO;
import com.pehrs.controlbooth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserRestController {

  private final ClientRegistration registration;
  private final UserService userService;

  public UserRestController(ClientRegistrationRepository registrations,
      @Value("${tg.registration.id:control-booth}") String registrationId,
      UserService userService) {
    this.registration = registrations.findByRegistrationId(registrationId);
    this.userService = userService;
  }


  @GetMapping(value = "/user-name", produces = "plain/text")
  public String userDetails() {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    return "name: " + auth.getName();
  }

  @GetMapping("/name")
  public Map<String, String> username(Authentication authentication) {
    String username = authentication.getName();
    log.info("username: {}",username);
    return Map.of("username", username);
  }

  @GetMapping("/claims")
  public Map<String, Object> idToken(@AuthenticationPrincipal OidcUser oidcUser) {
    log.info("oidcUser: {}", oidcUser);
    log.info("id token: {}", oidcUser.getIdToken().getTokenValue());

    if(oidcUser == null) {
      return Map.of("error", "No id_token found", "id_token", null);

    } else {
      return oidcUser.getClaims();
    }
  }

  @Getter
  @Builder
  public static class UserProfile {
    UserDTO user;
    Map<String, Object> attributes;
  }


  @GetMapping("/profile")
  public ResponseEntity<?> getUser(@AuthenticationPrincipal OAuth2User user) {
    log.debug("/api/user/profile");
    if (user == null) {
      return new ResponseEntity<>("", HttpStatus.OK);
    } else {
      UserProfileBuilder builder = UserProfile.builder()
          .attributes(user.getAttributes());

      userService.findUser(user.getName())
          .ifPresent(userDTO -> builder.user(userDTO));

      return ResponseEntity.ok().body(builder.build());
    }
  }


  @GetMapping("/login")
  public void loginRedirect(
      HttpServletResponse response,
      @RequestParam("to") String redirectTo,
      @AuthenticationPrincipal OAuth2User user)
      throws IOException {
    response.sendRedirect(redirectTo);
  }


  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletRequest request,
      @AuthenticationPrincipal(expression = "idToken") OidcIdToken idToken) {
    // send logout URL to client so they can initiate logout
    String logoutUrl = this.registration.getProviderDetails()
        .getConfigurationMetadata().get("end_session_endpoint").toString();

    Map<String, String> logoutDetails = new HashMap<>();
    logoutDetails.put("logoutUrl", logoutUrl);
    logoutDetails.put("idToken", idToken.getTokenValue());
    request.getSession(false).invalidate();
    return ResponseEntity.ok().body(logoutDetails);
  }
}
