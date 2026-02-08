package com.uva.api.auth.services;

import com.uva.api.auth.models.jwt.JwtAuth;
import com.uva.api.auth.models.jwt.JwtData;
import com.uva.api.auth.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

  private final JwtUtil jwtUtil;

  public boolean validateToken(String token) {
    return jwtUtil.validate(token) != null;
  }

  public ResponseEntity<?> identifyService(String name) {
    if (name == null)
      // TODO replace with exception handling
      return new ResponseEntity<>("Token has expire or is malformed", HttpStatus.FORBIDDEN);
    String token = jwtUtil.generateInternalToken(name);
    return ResponseEntity.ok(new JwtAuth(token));
  }

  public ResponseEntity<?> getTokenInf(String token) {
    JwtData decoded = jwtUtil.decodeToken(token);
    if (decoded == null)
      // TODO replace with exception handling
      return new ResponseEntity<>("Token has expire or is malformed", HttpStatus.FORBIDDEN);
    return ResponseEntity.ok(decoded);
  }
}
