package com.uva.api.auth.controllers;

import com.uva.api.auth.models.auth.ChangePasswordRequest;
import com.uva.api.auth.models.auth.LoginRequest;
import com.uva.api.auth.models.auth.RegisterRequest;
import com.uva.api.auth.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody @Validated LoginRequest loginRequest) {
    return authService.login(loginRequest);
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody @Validated RegisterRequest registerRequest) {
    return authService.register(registerRequest);
  }

  @PostMapping("/password")
  public ResponseEntity<?> changePassword(
          @RequestBody @Validated ChangePasswordRequest changePasswordRequest,
          @RequestHeader(value = "Authorization", required = true) String authorization
  ) {
    if (authorization == null || !authorization.startsWith("Bearer "))
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);

    String token = authorization.substring(7);

    return authService.changePassword(token, changePasswordRequest.email(), changePasswordRequest.oldPassword(), changePasswordRequest.newPassword());
  }

  @PatchMapping("/{id}/delete")
  public Object postMethodName(
          @PathVariable int id,
          @RequestBody Map<String, String> json,
          @RequestHeader(value = "Authorization", required = true) String authorization
  ) {
    if (authorization == null || !authorization.startsWith("Bearer "))
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);

    String token = authorization.substring(7);

    String actualPassword = json.get("password");

    return authService.deleteUser(token, id, actualPassword);
  }

}
