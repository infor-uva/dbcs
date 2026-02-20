package com.uva.api.auth.modules.auth.details;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class Oauth2UserInfoStrategy implements UserInfoStrategy {
  @Override
  public boolean support(Authentication authentication) {
    return authentication instanceof OAuth2AuthenticationToken;
  }

  @Override
  public Map<String, Object> getAuthDetails(Authentication authentication) {
    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    return Map.of("oauth", oAuth2User);
  }
}
