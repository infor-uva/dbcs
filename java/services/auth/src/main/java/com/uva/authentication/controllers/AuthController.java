package com.uva.authentication.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import com.uva.authentication.models.*;
import com.uva.authentication.services.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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

    private boolean validStrings(String... args) {
        for (String arg : args) {
            if (arg == null || arg.isBlank())
                return false;
        }
        return true;
    }

    @PostMapping("/password")
    public ResponseEntity<?> postMethodName(@RequestBody Map<String, String> json) {
        // TODO adaptar a comportamiento de admin
        String email = json.get("email");
        String actualPassword = json.get("actual");
        String newPassword = json.get("new");

        if (validStrings(email, actualPassword, newPassword))
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);

        try {
            // TODO extraer información del token?
            String token = authService.changePassword(email, actualPassword, newPassword);
            return ResponseEntity.ok(new JwtAuthResponse(token));
        } catch (Exception e) {
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }
    }
}
