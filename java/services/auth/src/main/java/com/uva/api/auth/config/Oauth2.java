package com.uva.api.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;

@Configuration
public class Oauth2 {
  @Bean
  OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
    return request -> {
      OAuth2User oauthUser = new DefaultOAuth2UserService().loadUser(request);

      String email = oauthUser.getAttribute("email");
      String provider = request.getClientRegistration().getRegistrationId();
      String providerUserId = oauthUser.getName();

      // User user = userService.processOAuthLogin(email, provider, providerUserId);

      return new DefaultOAuth2User(
          List.of(new SimpleGrantedAuthority("ROLE_USER")),
          oauthUser.getAttributes(),
          "email");
    };
  }
}
