package com.uva.authentication.utils;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.uva.authentication.models.remote.User;

@Component
public class JwtUtil {

  @Value("${security.jwt.secret-key}")
  private String secretKey;

  @Value("${security.jwt.kid}")
  private String kid;

  @Value("${security.jwt.expiration-time}")
  private long jwtExpiration;

  public long getExpirationTime() {
    return jwtExpiration;
  }

  public String generateToken(User user) {
    Algorithm algorithm = Algorithm.HMAC256(secretKey);
    return JWT
        .create()
        .withKeyId(kid)
        .withClaim("id", user.getId())
        .withClaim("name", user.getName())
        .withClaim("email", user.getEmail())
        .withClaim("rol", user.getRol().toString())
        .withIssuedAt(new Date())
        .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpiration * 1000))
        .sign(algorithm);
  }

  // TODO estaría guapo recuperar métodos de validación para el token de petición
  // para este servicio
}
