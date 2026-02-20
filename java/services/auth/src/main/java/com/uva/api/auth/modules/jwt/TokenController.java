package com.uva.api.auth.modules.jwt;

import com.uva.api.auth.modules.jwt.dto.TokenPairResponse;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

@RestController
@RequestMapping("token")
@RequiredArgsConstructor
public class TokenController {

  private final JwtUtil jwtUtil;
  private final TokenService tokenService;

  /**
   * Validate a token and return its claims.
   */
  @PostMapping("/validate")
  public ResponseEntity<?> validateToken(@RequestBody Map<String, String> body) {
    String token = body.get("token");
    Claims claims = jwtUtil.validateAndParseClaims(token);
    if (claims == null) {
      throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Token not valid or expired");
    }
    return ResponseEntity.ok(claims);
  }

  /**
   * Refresh tokens — revokes old refresh token and issues a new pair.
   */
  @PostMapping("/refresh")
  public ResponseEntity<TokenPairResponse> refreshToken(@RequestBody Map<String, String> body) {
    String refreshToken = body.get("refreshToken");
    return ResponseEntity.ok(tokenService.refresh(refreshToken));
  }
}
