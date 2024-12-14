package com.uva.monolith.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import com.uva.monolith.services.hotels.models.external.users.UserRol;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.Filter;
import java.io.IOException;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;

@Component
public class JwtAuthenticationFilter implements Filter {

    private final String SECRET_KEY = "3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b";

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return null;
        return authHeader.substring(7);
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean validateToken(String token) {
        if (token == null)
            return false;// no token
        try {
            // Verifica y analiza el token
            Claims claims = getClaimsFromToken(token);

            // Verifica que el token no esté expirado
            return claims.getExpiration().after(new Date());
        } catch (ExpiredJwtException e) {
            System.out.println("[" + LocalDateTime.now().toString() + "] Token expirado: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("[" + LocalDateTime.now().toString() + "] Token no soportado: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("[" + LocalDateTime.now().toString() + "] Token malformado: " + e.getMessage());
        } catch (SignatureException e) {
            System.out.println("[" + LocalDateTime.now().toString() + "] Firma inválida: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("[" + LocalDateTime.now().toString() + "] Token vacío o nulo: " + e.getMessage());
        }
        return false; // Si ocurre cualquier excepción, el token es inválido

    }

    private String getEmailFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    private UserRol getRoleFromToken(String token) {
        String rol = getClaimsFromToken(token).get("rol", String.class);
        return UserRol.valueOf(rol);
    }

    public static String getRol(UserRol rol) {
        return String.format("ROLE_%s", rol.toString());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = getTokenFromRequest(httpRequest);

        System.out.println("[" + LocalDateTime.now().toString() + "] TOKEN " + token);

        if (validateToken(token)) {

            String email = getEmailFromToken(token);
            UserRol role = getRoleFromToken(token); // Extraer el rol del token

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        email, null, null);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            // Agregar el rol como autoridad
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(getRol(role));
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, null,
                    Collections.singletonList(authority));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }

}
