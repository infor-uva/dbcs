package com.uva.authentication.Controllers;

import com.uva.authentication.Models.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private RestTemplate restTemplate;  // Usamos RestTemplate para acceder al microservicio de usuarios

    private final String USER_SERVICE_URL = "http://user-service/users";  // URL del microservicio de usuarios

    // Clave secreta para firmar el token JWT
    private final String SECRET_KEY = "your-secret-key";  // Cambia esto por una clave secreta segura

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // Hacemos una llamada al microservicio de usuarios para obtener el usuario por correo
        ResponseEntity<User> userResponse = restTemplate.getForEntity(
                USER_SERVICE_URL + "/{email}",
                User.class,
                loginRequest.getEmail()
        );

        // Verificamos si el usuario existe
        if (userResponse.getStatusCode().is2xxSuccessful()) {
            User user = userResponse.getBody();
            // Verificamos que la contraseña coincida (esto debería hacerse de forma segura en un servicio real)
            if (user != null && user.getPassword().equals(loginRequest.getPassword())) {
                // Generamos el token JWT si la autenticación es correcta
                String token = generateToken(user.getName(), user.getEmail());
                return ResponseEntity.ok(new AuthResponse(token));
            }
        }

        // Si el usuario no existe o la contraseña es incorrecta
        return ResponseEntity.status(401).body("Invalid email or password");
    }

    // Método para generar el token JWT
    private String generateToken(String name, String email) {
        return Jwts.builder()
                .setSubject(name)
                .claim("email", email)  // Añadimos el correo como parte del payload
                .setIssuedAt(new Date())  // Fecha de emisión
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))  // Expira en 1 hora
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)  // Firma con la clave secreta
                .compact();
    }

    public static class LoginRequest {
        private String email;
        private String password;

        // Getters and setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class AuthResponse {
        private String token;

        public AuthResponse(String token) {
            this.token = token;
        }

        // Getter
        public String getToken() {
            return token;
        }
    }
}
