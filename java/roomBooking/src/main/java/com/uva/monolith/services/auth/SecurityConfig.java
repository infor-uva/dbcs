package com.uva.monolith.services.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
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
                        .requestMatchers("/users").access((authentication, context) -> {
                            String method = context.getRequest().getMethod();
                            String email = context.getRequest().getParameter("email");
                            // Permitir POST a /users solo al rol ADMIN
                            boolean register = method.equals("POST") && authentication.get().getAuthorities().stream()
                                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
                            // Permitir GET a /users con parámetro email solo al rol ADMIN
                            boolean access = method.equals("GET") && email != null && !email.isEmpty() &&
                                    authentication.get().getAuthorities().stream()
                                            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
                            return new AuthorizationDecision(register || access);
                        })
                        .requestMatchers("/users/**").hasRole(UserRol.CLIENT.toString())
                        .requestMatchers("/hotels/**", "/booking/**").permitAll() //
                // .requestMatchers("/users/**", "/hotels/**", "/booking/**").authenticated() //
                // Protegidas
                )
                // Registra el filtro antes del filtro estándar de autenticación
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
