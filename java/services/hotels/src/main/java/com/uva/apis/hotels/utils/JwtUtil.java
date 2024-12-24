package com.uva.monolith.utils;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.uva.monolith.models.external.users.UserRol;

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

  public String generateToken(String name, String email, UserRol rol) {
    Algorithm algorithm = Algorithm.HMAC256(secretKey);
    return JWT
        .create()
        .withKeyId(kid)
        .withClaim("name", name)
        .withClaim("email", email)
        .withClaim("rol", rol.toString())
        .withIssuedAt(new Date())
        .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpiration * 1000))
        .sign(algorithm);
  }

  // TODO estaría guapo recuperar métodos de validación para el token de petición
  // para este servicio
}
