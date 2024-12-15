package com.uva.authentication.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import com.uva.authentication.models.*;
import com.uva.authentication.services.AuthService;

@RestController
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            String token = authService.login(loginRequest);
            return ResponseEntity.ok(new JwtAuthResponse(token));
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<String>("Algo no fue bien", HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            String token = authService.register(registerRequest);
            return ResponseEntity.ok(new JwtAuthResponse(token));
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
            }
            e.fillInStackTrace();
        }

        return new ResponseEntity<String>("Algo no fue bien", HttpStatus.UNAUTHORIZED);
    }

}
