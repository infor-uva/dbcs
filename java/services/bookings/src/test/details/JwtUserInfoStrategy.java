package com.uva.api.auth.modules.auth.details;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JwtUserInfoStrategy implements UserInfoStrategy {

  @Override
  public boolean support(Authentication authentication) {
    return authentication instanceof JwtAuthenticationToken;
  }

  @Override
  public Map<String, Object> getAuthDetails(Authentication authentication) {
    Jwt jwt = (Jwt) authentication.getPrincipal();
    return Map.of("jwt", jwt);
  }
}
