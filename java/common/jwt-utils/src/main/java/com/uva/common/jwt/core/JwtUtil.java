package com.uva.common.jwt.core;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.time.Instant;

public class JwtUtil {

  private final String secretKey;

  public JwtUtil(String secretKey) {
    this.secretKey = secretKey;
  }

  public DecodedJWT validate(String token) {
    try {
      return JWT.require(Algorithm.HMAC256(secretKey)).build().verify(token);
    } catch (Exception e) {
      return null;
    }
  }

  public JwtData decodeToken(String token) {
    DecodedJWT decoded = validate(token);
    if (decoded == null) {
      return null;
    }

    JwtData data = new JwtData();
    data.setToken(token);
    data.setId(decoded.getClaim("id").asInt() != null ? decoded.getClaim("id").asInt() : -1);
    data.setName(decoded.getClaim("name").asString());
    data.setEmail(decoded.getClaim("email").asString());

    try {
      String roleClaim = decoded.getClaim("rol").asString();
      if (roleClaim != null) {
        data.setRol(UserRol.valueOf(roleClaim));
      }
    } catch (Exception ignored) {
    }

    try {
      String serviceClaim = decoded.getClaim("service").asString();
      if (serviceClaim != null) {
        data.setService(Service.valueOf(serviceClaim));
      }
    } catch (Exception ignored) {
    }

    data.setSubject(decoded.getSubject());
    data.setAudience(
        decoded.getAudience() != null && !decoded.getAudience().isEmpty() ? decoded.getAudience().get(0) : null);
    data.setTtl(calculateTTL(decoded));
    data.setIssuedAt(decoded.getIssuedAt());
    data.setExpiresAt(decoded.getExpiresAt());

    return data;
  }

  private long calculateTTL(DecodedJWT decodedJWT) {
    if (decodedJWT == null || decodedJWT.getExpiresAt() == null) {
      return 0;
    }
    long exp = decodedJWT.getExpiresAt().toInstant().getEpochSecond();
    long now = Instant.now().getEpochSecond();
    return exp - now;
  }
}
