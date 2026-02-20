package com.uva.api.auth.modules.jwt;

import com.uva.api.auth.modules.jwt.dto.TokenPairResponse;
import com.uva.api.auth.modules.user.UserEntity;
import com.uva.api.auth.modules.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TokenService {

  private final JwtUtil jwtUtil;
  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;

  /**
   * Issue an access + refresh token pair for the given user.
   */
  @Transactional
  public TokenPairResponse issueTokenPair(UserEntity user) {
    String accessToken = jwtUtil.generateAccessToken(user);

    // Create and persist the refresh token
    RefreshToken refreshToken = RefreshToken.builder()
        .token(jwtUtil.generateRefreshTokenValue())
        .email(user.getEmail())
        .expiresAt(Instant.now().plusSeconds(jwtUtil.getRefreshTokenExpiration()))
        .build();

    refreshTokenRepository.save(refreshToken);

    return new TokenPairResponse(
        accessToken,
        refreshToken.getToken(),
        jwtUtil.getAccessTokenExpiration());
  }

  /**
   * Validate the refresh token, revoke it (rotation), and issue a new pair.
   */
  @Transactional
  public TokenPairResponse refresh(String refreshTokenValue) {
    RefreshToken stored = refreshTokenRepository
        .findByTokenAndRevokedFalse(refreshTokenValue)
        .orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

    if (stored.getExpiresAt().isBefore(Instant.now())) {
      stored.setRevoked(true);
      refreshTokenRepository.save(stored);
      throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
    }

    // Revoke the old token (rotation)
    stored.setRevoked(true);
    refreshTokenRepository.save(stored);

    // Look up the user and issue a new pair
    UserEntity user = userRepository.findByEmail(stored.getEmail())
        .orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "User not found"));

    return issueTokenPair(user);
  }

  /**
   * Revoke a refresh token (logout).
   */
  @Transactional
  public void revoke(String refreshTokenValue) {
    refreshTokenRepository.findByTokenAndRevokedFalse(refreshTokenValue)
        .ifPresent(token -> {
          token.setRevoked(true);
          refreshTokenRepository.save(token);
        });
  }
}
