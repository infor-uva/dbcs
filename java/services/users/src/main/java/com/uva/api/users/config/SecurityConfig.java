package com.uva.api.users.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.http.HttpMethod.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.uva.api.users.models.UserRol;
import com.uva.api.users.models.remote.jwt.Service;
import static com.uva.api.users.models.remote.jwt.Service.*;
import static com.uva.api.users.models.UserRol.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
  }

  private final String[] SERVICES = flat(Service.values());

  private String[] flat(UserRol... roles) {
    return java.util.Arrays.stream(roles)
        .map(Enum::toString)
        .map(role -> String.format("ROLE_%s", role))
        .toArray(String[]::new);
  }

  private String[] flat(Service... services) {
    return java.util.Arrays.stream(services)
        .map(Enum::toString)
        .toArray(String[]::new);
  }

  private String[] join(String[]... authority) {
    return java.util.Arrays.stream(authority)
        .flatMap(java.util.Arrays::stream)
        .toArray(String[]::new);
  }

  /**
   * All services and specified roles
   * 
   * @param roles
   * @return
   */
  private String[] anyService(UserRol... roles) {
    return join(flat(roles), SERVICES);
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    String id = "{id:\\d+}";

    http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(authorize -> authorize
            // Permitir OPTIONS sin autenticación
            .requestMatchers(OPTIONS, "/**").permitAll()
            // Restringir acceso
            // Solo permitimos actualizar el estado al servicio de reservas
            .requestMatchers(PATCH, "/users/clients/" + id)
            .hasAuthority(BOOKINGS.toString())

            .requestMatchers(DELETE, "/users/{id}")
            .hasAnyAuthority(join(flat(ADMIN), flat(AUTHENTICATION)))

            .requestMatchers("/users/clients/**")
            .hasAnyAuthority(anyService(ADMIN, CLIENT))

            .requestMatchers("/users/managers/**")
            .hasAnyAuthority(anyService(ADMIN, MANAGER))

            .requestMatchers("/users*", "/users/clients", "/users/managers")
            .hasAnyAuthority(anyService(ADMIN))

            // Para las operaciones concretas de los usuarios se permite el acceso
            // a los que estén autentificados se limita el acceso en el servicio
            .requestMatchers("/users/" + id + "/**").authenticated()

            // Rechazar el resto
            .anyRequest().denyAll())
        // Registra el filtro antes del filtro estándar de autenticación
        .addFilterBefore(jwtAuthenticationFilter,
            UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
