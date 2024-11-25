package com.uva.monolith.services.auth;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.uva.monolith.services.users.controllers.UserService;
import com.uva.monolith.services.users.models.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final String SECRET_KEY = "clave_secreta";

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest loginRequest) {
        User user = userService.findByEmail(loginRequest.getEmail());

        if (user != null && userService.verifyPassword(loginRequest.getPassword(), user.getPassword())) {
            String token = Jwts.builder()
                               .setSubject(user.getEmail())
                               .signWith(new SecretKeySpec(SECRET_KEY.getBytes(), SignatureAlgorithm.HS256.getJcaName()))
                               .claim("email", user.getEmail())
                               .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hora
                               .compact();

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return response;
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Credenciales inválidas");
    }
}
