package com.uva.authentication.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.uva.authentication.models.jwt.JwtAuth;
import com.uva.authentication.models.jwt.JwtData;
import com.uva.authentication.utils.JwtUtil;

@Service
public class TokenService {

  @Autowired
  private JwtUtil jwtUtil;

  public boolean validateToken(String token) {
    return jwtUtil.validate(token) != null;
  }

  public ResponseEntity<?> identifyService(String name) {
    if (name == null)
      return new ResponseEntity<>("Token has expire or is malformed", HttpStatus.FORBIDDEN);
    String token = jwtUtil.generateInternalToken(name);
    return ResponseEntity.ok(new JwtAuth(token));
  }

  public ResponseEntity<?> getTokenInf(String token) {
    JwtData decoded = jwtUtil.decodeToken(token);
    if (decoded == null)
      return new ResponseEntity<>("Token has expire or is malformed", HttpStatus.FORBIDDEN);
    return ResponseEntity.ok(decoded);
  }
}
