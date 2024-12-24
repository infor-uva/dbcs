package com.uva.api.bookings.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.uva.api.bookings.filter.JwtAuthenticationFilter;
import com.uva.api.bookings.models.external.users.UserRol;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable());
    // .authorizeHttpRequests(authorize -> authorize
    // // Permitir OPTIONS sin autenticación
    // .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
    // // Acceso restringido a usuarios y administradores
    // .requestMatchers("users", "users/**").hasAnyRole(
    // UserRol.CLIENT.toString(),
    // UserRol.HOTEL_ADMIN.toString(),
    // UserRol.ADMIN.toString())
    // // Acceso restringido a gestores de hoteles y administradores
    // .requestMatchers(HttpMethod.GET, "hotels", "hotels/*").hasAnyRole(
    // UserRol.CLIENT.toString(),
    // UserRol.HOTEL_ADMIN.toString(),
    // UserRol.ADMIN.toString())

    // .requestMatchers("hotels", "hotels/**")
    // .hasAnyRole(UserRol.ADMIN.toString(), UserRol.HOTEL_ADMIN.toString())
    // // Acceso restringido a cualquier usuario del sistema
    // .requestMatchers("bookings", "bookings/**")
    // .hasAnyRole(UserRol.ADMIN.toString(), UserRol.HOTEL_ADMIN.toString(),
    // UserRol.CLIENT.toString())
    // // Rechazar el resto
    // .anyRequest().denyAll())
    // // Registra el filtro antes del filtro estándar de autenticación
    // .addFilterBefore(jwtAuthenticationFilter,
    // UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
