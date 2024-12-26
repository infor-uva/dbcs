package com.uva.api.users.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import com.uva.api.users.models.UserRol;
import com.uva.api.users.models.remote.JwtData;
import com.uva.api.users.services.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.Filter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

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
                    "[" + LocalDateTime.now().toString() + "] Error de verificación del token");
            ex.printStackTrace(System.err);
            return null;
        }
    }

    private String formatRole(UserRol rol) {
        return String.format("ROLE_%s", rol.toString());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = getTokenFromRequest(httpRequest);

        System.out.println("[" + LocalDateTime.now().toString() + "] TOKEN: " + token);

        if (token != null) {
            JwtData jwt = validateAndDecodeToken(token);
            if (jwt != null) {
                System.out.println("-->" + jwt + "<--");
            }
        }

        // String email = getEmailFromToken(jwt);
        // UserRol role = getRoleFromToken(jwt);
        // System.out.print(" email=" + email + " role=" + role + " ");

        // if (email != null && role != null &&
        // SecurityContextHolder.getContext().getAuthentication() == null) {
        // // Crear la autoridad con el rol del token
        // SimpleGrantedAuthority authority = new
        // SimpleGrantedAuthority(formatRole(role));

        // // Crear autenticación
        // UsernamePasswordAuthenticationToken authentication = new
        // UsernamePasswordAuthenticationToken(email,
        // null, Collections.singletonList(authority));
        // authentication.setDetails(new
        // WebAuthenticationDetailsSource().buildDetails(httpRequest));

        // // Establecer autenticación en el contexto de seguridad
        // SecurityContextHolder.getContext().setAuthentication(authentication);
        // }
        // }
        // }

        // Continuar con el resto de filtros
        chain.doFilter(request, response);
    }
}
