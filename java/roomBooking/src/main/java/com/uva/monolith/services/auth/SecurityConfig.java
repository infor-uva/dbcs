package com.uva.monolith.services.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.uva.monolith.services.users.models.UserRol;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        // Permitir todas las conexiones
                        .requestMatchers("").permitAll()
                        // Acceso restringido a usuarios y administradores
                        .requestMatchers("users", "users/**")
                        .hasAnyRole(UserRol.ADMIN.toString(), UserRol.CLIENT.toString())
                        // Acceso restringido a gestores de hoteles y administradores
                        .requestMatchers("hotels", "hotels/**")
                        .hasAnyRole(UserRol.ADMIN.toString(), UserRol.HOTEL_ADMIN.toString())
                        // Acceso restringido a cualquier usuario del sistema
                        .requestMatchers("bookings", "bookings/**")
                        .hasAnyRole(UserRol.ADMIN.toString(), UserRol.HOTEL_ADMIN.toString(), UserRol.CLIENT.toString())
                        // Rechazar el resto
                        .anyRequest().denyAll())
                // Registra el filtro antes del filtro estándar de autenticación
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
