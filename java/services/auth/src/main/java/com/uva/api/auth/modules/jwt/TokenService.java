package com.uva.api.auth.modules.jwt;

import com.uva.api.auth.modules.jwt.dto.JwtAuthRequest;
import com.uva.api.auth.modules.jwt.dto.JwtDataResponse;
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
    return ResponseEntity.ok(new JwtAuthRequest(token));
  }

  public ResponseEntity<?> getTokenInf(String token) {
    JwtDataResponse decoded = jwtUtil.decodeToken(token);
    if (decoded == null)
      // TODO replace with exception handling
      return new ResponseEntity<>("Token has expire or is malformed", HttpStatus.FORBIDDEN);
    return ResponseEntity.ok(decoded);
  }
}
