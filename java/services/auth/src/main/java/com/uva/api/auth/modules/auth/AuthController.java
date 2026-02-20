package com.uva.api.auth.modules.auth;

import com.uva.api.auth.modules.auth.dto.LoginRequest;
import com.uva.api.auth.modules.auth.dto.RegisterRequest;
import com.uva.api.auth.modules.jwt.TokenService;
import com.uva.api.auth.modules.jwt.dto.TokenPairResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final TokenService tokenService;

  @PostMapping("/login")
  public ResponseEntity<@NotNull TokenPairResponse> login(@RequestBody @Validated LoginRequest loginRequest) {
    return ResponseEntity.ok(authService.login(loginRequest));
  }

  @PostMapping("/register")
  public ResponseEntity<@NotNull TokenPairResponse> register(@RequestBody @Validated RegisterRequest registerRequest) {
    return ResponseEntity.ok(authService.register(registerRequest));
  }

  @PostMapping("/refresh")
  public ResponseEntity<@NotNull TokenPairResponse> refresh(@RequestBody java.util.Map<String, String> body) {
    String refreshToken = body.get("refreshToken");
    return ResponseEntity.ok(tokenService.refresh(refreshToken));
  }

  @PostMapping("/logout")
  public ResponseEntity<@NotNull Void> logout(@RequestBody java.util.Map<String, String> body) {
    String refreshToken = body.get("refreshToken");
    tokenService.revoke(refreshToken);
    return ResponseEntity.noContent().build();
  }
}
