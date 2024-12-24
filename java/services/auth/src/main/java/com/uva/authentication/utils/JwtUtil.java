package com.uva.authentication.utils;

import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.uva.authentication.models.jwt.JwtData;
import com.uva.authentication.models.remote.User;

import com.auth0.jwt.interfaces.DecodedJWT;

import java.time.Instant;

@Component
public class JwtUtil {

  @Value("${security.jwt.kid}")
  private String kid;

  @Value("${security.jwt.secret-key}")
  private String secretKey;

  @Value("${security.jwt.internal.expiration}")
  private long intJwtExpiration;

  @Value("${security.jwt.external.expiration}")
  private long extJwtExpiration;

  private String token;

  @Value("${spring.application.name}")
  private String service;

  public String getOwnInternalToken() {

    // Si no hay token, no es valido o quedan 10 seg para caducar se genera otro
    if (token == null || validate(token) == null ||
        decodeToken(token).getTtl() <= 10) {
      token = generateInternalToken(service);
    }

    return token;

  }

  public String generateInternalToken(String service) {
    String email = service.toLowerCase() + "@internal.com";
    Algorithm algorithm = Algorithm.HMAC256(secretKey);

    return JWT
        .create()

        .withKeyId(kid)
        .withIssuedAt(new Date())
        .withExpiresAt(new Date(System.currentTimeMillis() + intJwtExpiration * 1000))

        .withSubject(service)
        .withAudience("INTERNAL")

        // DATA
        .withClaim("service", service)
        .withClaim("email", email)

        .sign(algorithm);
  }

  public String generateToken(User user) {
    Algorithm algorithm = Algorithm.HMAC256(secretKey);

    System.out.println("\n\n<-- " + user + " " + user.getId() + " -->");

    return JWT
        .create()

        .withKeyId(kid)
        .withIssuedAt(new Date())
        .withExpiresAt(new Date(System.currentTimeMillis() + extJwtExpiration * 1000))

        .withSubject(service)
        .withAudience("EXTERNAL")

        // DATA
        .withClaim("id", user.getId())
        .withClaim("name", user.getName())
        .withClaim("email", user.getEmail())
        .withClaim("rol", user.getRol().toString())

        .sign(algorithm);
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
    if (decoded == null)
      return null;
    return new JwtData(decoded, calculateTTL(decoded));
  }

  private long calculateTTL(DecodedJWT decodedJWT) {
    if (decodedJWT == null)
      throw new HttpClientErrorException(HttpStatus.FORBIDDEN);

    long exp = decodedJWT.getExpiresAt().toInstant().getEpochSecond();
    long now = Instant.now().getEpochSecond();

    return exp - now;
  }

}
