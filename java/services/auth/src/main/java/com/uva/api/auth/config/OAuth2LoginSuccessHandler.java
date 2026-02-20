package com.uva.api.auth.config;

import com.uva.api.auth.modules.jwt.TokenService;
import com.uva.api.auth.modules.jwt.dto.TokenPairResponse;
import com.uva.api.auth.modules.user.AuthProvider;
import com.uva.api.auth.modules.user.UserEntity;
import com.uva.api.auth.modules.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * After a successful OAuth2 login (GitHub/Google), find-or-create the user
 * in the local database and respond with a JWT token pair.
 */
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

  private final UserRepository userRepository;
  private final TokenService tokenService;
  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationSuccess(
    @NonNull HttpServletRequest request,
    @NonNull HttpServletResponse response,
    @NonNull Authentication authentication) throws IOException {

    OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
    OAuth2User oAuth2User = oauthToken.getPrincipal();
    if (oAuth2User == null) {
      // TODO improve handling
      return;
    }

    String registrationId = oauthToken.getAuthorizedClientRegistrationId();
    AuthProvider provider = AuthProvider.valueOf(registrationId.toUpperCase());

    String email = oAuth2User.getAttribute("email");
    String name = oAuth2User.getAttribute("name");
    String providerUserId = oAuth2User.getName(); // provider's unique ID

    // Find or create the user
    UserEntity user = userRepository.findByEmail(email)
      .orElseGet(() -> {
        UserEntity newUser = UserEntity.builder()
          .email(email)
          .name(name != null ? name : email)
          .provider(provider)
          .providerUserId(providerUserId)
          .build();
        return userRepository.save(newUser);
      });

    // Issue token pair
    TokenPairResponse tokenPair = tokenService.issueTokenPair(user);

    // Write JSON response
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    objectMapper.writeValue(response.getOutputStream(), tokenPair);
  }
}
