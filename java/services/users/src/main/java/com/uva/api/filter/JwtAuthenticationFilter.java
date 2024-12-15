package com.uva.api.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.uva.api.models.UserRol;
import com.auth0.jwt.exceptions.JWTVerificationException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.Filter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter implements Filter {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    private Algorithm getSigningAlgorithm() {
        return Algorithm.HMAC256(secretKey); // Usar HMAC256 con la clave secreta
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }

    private DecodedJWT validateAndDecodeToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(getSigningAlgorithm()).build();
            return verifier.verify(token); // Verifica y decodifica el token
        } catch (JWTVerificationException ex) {
            System.out.println(
                    "[" + LocalDateTime.now().toString() + "] Error de verificación del token: " + ex.getMessage());
            return null;
        }
    }

    private String getEmailFromToken(DecodedJWT jwt) {
        return jwt.getClaim("email").asString();
    }

    private UserRol getRoleFromToken(DecodedJWT jwt) {
        String role = jwt.getClaim("rol").asString();
        return UserRol.valueOf(role);
    }

    private String formatRole(UserRol rol) {
        return String.format("ROLE_%s", rol.toString());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = getTokenFromRequest(httpRequest);

        System.out.print("[" + LocalDateTime.now().toString() + "] TOKEN: " + token);

        if (token != null) {
            DecodedJWT jwt = validateAndDecodeToken(token);
            System.out.print(" " + jwt.toString() + " ");
            if (jwt != null) {
                String email = getEmailFromToken(jwt);
                UserRol role = getRoleFromToken(jwt);
                System.out.print(" email=" + email + " role=" + role + " ");

                if (email != null && role != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Crear la autoridad con el rol del token
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(formatRole(role));

                    // Crear autenticación
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email,
                            null, Collections.singletonList(authority));
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));

                    // Establecer autenticación en el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        // Continuar con el resto de filtros
        chain.doFilter(request, response);
    }
}
