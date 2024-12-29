package com.uva.api.auth.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uva.api.auth.models.auth.LoginRequest;
import com.uva.api.auth.models.auth.RegisterRequest;
import com.uva.api.auth.services.AuthService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    @PostMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> json,
            @RequestHeader(value = "Authorization", required = true) String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer "))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        String token = authorization.substring(7);

        String email = json.get("email");
        String actualPassword = json.get("password");
        String newPassword = json.get("newPassword");

        return authService.changePassword(token, email, actualPassword, newPassword);
    }

    @PostMapping("/delete/{id}")
    public Object postMethodName(@PathVariable int id, @RequestBody Map<String, String> json,
            @RequestHeader(value = "Authorization", required = true) String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer "))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        String token = authorization.substring(7);

        String actualPassword = json.get("password");

        return authService.deleteUser(token, id, actualPassword);
    }

}
