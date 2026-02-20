package com.uva.api.auth.modules.jwt;

import com.uva.api.auth.modules.user.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

  private final SecretKey signingKey;
  private final long accessTokenExpiration;
  private final long refreshTokenExpiration;
  private final String kid;

  public JwtUtil(
      @Value("${security.jwt.secret-key}") String secretKey,
      @Value("${security.jwt.kid}") String kid,
      @Value("${security.jwt.expiration.access}") long accessTokenExpiration,
      @Value("${security.jwt.expiration.refresh}") long refreshTokenExpiration) {
    this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    this.kid = kid;
    this.accessTokenExpiration = accessTokenExpiration;
    this.refreshTokenExpiration = refreshTokenExpiration;
  }

  /**
   * Generate a short-lived access token containing user claims.
   */
  public String generateAccessToken(UserEntity user) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + accessTokenExpiration * 1000);

    return Jwts.builder()
        .header().keyId(kid).and()
        .subject(user.getEmail())
        .issuedAt(now)
        .expiration(expiry)
        .claim("id", user.getId())
        .claim("name", user.getName())
        .claim("email", user.getEmail())
        .claim("rol", user.getRol().name())
        .claim("provider", user.getProvider().name())
        .signWith(signingKey)
        .compact();
  }

  /**
   * Generate a random refresh token string (UUID-based, not a JWT).
   * The actual expiration is tracked in the database.
   */
  public String generateRefreshTokenValue() {
    return UUID.randomUUID().toString();
  }

  /**
   * @return refresh token expiration in seconds
   */
  public long getRefreshTokenExpiration() {
    return refreshTokenExpiration;
  }

  /**
   * @return access token expiration in seconds
   */
  public long getAccessTokenExpiration() {
    return accessTokenExpiration;
  }

  /**
   * Validate and parse the access token. Returns claims if valid, null otherwise.
   */
  public Claims validateAndParseClaims(String token) {
    try {
      return Jwts.parser()
          .verifyWith(signingKey)
          .build()
          .parseSignedClaims(token)
          .getPayload();
    } catch (JwtException | IllegalArgumentException e) {
      return null;
    }
  }
}
