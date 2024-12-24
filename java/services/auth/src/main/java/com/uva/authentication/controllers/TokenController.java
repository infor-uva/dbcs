package com.uva.authentication.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.uva.authentication.models.jwt.JwtAuth;
import com.uva.authentication.services.TokenService;

@RestController
@RequestMapping("token")
public class TokenController {

  @Autowired
  private TokenService tokenService;

  @PostMapping("/validate")
  public ResponseEntity<?> validateToken(@RequestBody JwtAuth tokenRequest) {
    boolean isValid = tokenService.validateToken(tokenRequest.getToken());
    if (isValid) {
      return ResponseEntity.ok("Token is valid");
    } else {
      return new ResponseEntity<>("Token not valid or expired", HttpStatus.UNAUTHORIZED);
    }
  }

  @PostMapping("/info")
  public ResponseEntity<?> getTokenInfo(@RequestBody JwtAuth tokenRequest) {
    return tokenService.getTokenInf(tokenRequest.getToken());
  }

  @PostMapping("/service")
  public ResponseEntity<?> identifyService(@RequestBody JsonNode request) {
    JsonNode name = request.get("service");

    if (name == null)
      return new ResponseEntity<>("Missing required fields", HttpStatus.BAD_REQUEST);

    return tokenService.identifyService(name.asText());
  }

}
