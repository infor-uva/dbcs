package com.uva.api.auth.modules.jwt;

import com.fasterxml.jackson.databind.JsonNode;
import com.uva.api.auth.modules.jwt.dto.JwtAuthRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("token")
public class TokenController {

  @Autowired
  private TokenService tokenService;

  @PostMapping("/validate")
  public ResponseEntity<?> validateToken(@RequestBody JwtAuthRequest tokenRequest) {
    boolean isValid = tokenService.validateToken(tokenRequest.getToken());
    if (isValid) {
      return ResponseEntity.ok("Token is valid");
    } else {
      return new ResponseEntity<>("Token not valid or expired", HttpStatus.UNAUTHORIZED);
    }
  }

  @PostMapping("/info")
  public ResponseEntity<?> getTokenInfo(@RequestBody JwtAuthRequest tokenRequest) {
    return tokenService.getTokenInf(tokenRequest.getToken());
  }

  @PostMapping("/service") // TODO revisar
  public ResponseEntity<?> identifyService(@RequestBody JsonNode request) {
    JsonNode name = request.get("service");

    if (name == null)
      return new ResponseEntity<>("Missing required fields", HttpStatus.BAD_REQUEST);

    return tokenService.identifyService(name.asText());
  }

}
