package com.uva.api.users.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import com.uva.api.users.models.UserRol;
import com.uva.api.users.models.remote.jwt.JwtData;
import com.uva.api.users.models.remote.jwt.Service;
import com.uva.api.users.services.TokenService;

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

    private final TokenService service;

    public JwtAuthenticationFilter(TokenService service) {
        this.service = service;
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }

    private JwtData validateAndDecodeToken(String token) {
        try {
            return service.decodeToken(token);
        } catch (Exception ex) {
            System.err.println(
                    "[" + LocalDateTime.now().toString() + "] Error de verificación del token\n");
            ex.printStackTrace(System.err);
            return null;
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = getTokenFromRequest(httpRequest);

        System.out.println("[" + LocalDateTime.now().toString() + "] TOKEN: " + token + "\n");

        if (token != null) {
            JwtData jwt = validateAndDecodeToken(token);
            if (jwt != null) {
                String email = jwt.getEmail();
                UserRol role = jwt.getRol();
                Service service = jwt.getService();
                String audience = jwt.getAudience();

                System.out.println("[" + LocalDateTime.now().toString() + "] email=" + email + " role=" + role
                        + " service=" + service + " audience=" + audience + "\n");

                if (audience != null) {
                    // Definimos la autoridad
                    String authorityValue = null;
                    if (audience.equals("INTERNAL") && service != null) {
                        authorityValue = service.toString();
                    } else if (audience.equals("EXTERNAL") && role != null) {
                        authorityValue = String.format("ROLE_%s", role);
                    }

                    if (authorityValue != null &&
                            SecurityContextHolder.getContext().getAuthentication() == null) {

                        // Crear la autoridad con la autoridad oportuna
                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(authorityValue);

                        // Crear autenticación
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                email,
                                null, Collections.singletonList(authority));
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));

                        // Establecer autenticación en el contexto de seguridad
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        }

        // Continuar con el resto de filtros
        chain.doFilter(request, response);
    }
}
