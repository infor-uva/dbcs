package com.uva.api.auth.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

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
        try {
            return authService.login(loginRequest);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Algo no fue bien", HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            return authService.register(registerRequest);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.CONFLICT)
                return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>("Algo no fue bien", HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> json,
            @RequestHeader(value = "Authorization", required = true) String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer "))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        String token = authorization.substring(7);

        String actualPassword = json.get("password");
        String newPassword = json.get("newPassword");

        return authService.changePassword(token, actualPassword, newPassword);
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
