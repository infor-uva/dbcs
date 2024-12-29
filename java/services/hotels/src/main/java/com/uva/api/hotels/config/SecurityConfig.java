package com.uva.api.hotels.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import static org.springframework.http.HttpMethod.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.uva.api.hotels.filter.JwtAuthenticationFilter;
import com.uva.api.hotels.models.external.jwt.Service;
import com.uva.api.hotels.models.external.users.UserRol;
import static com.uva.api.hotels.models.external.users.UserRol.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final String[] SERVICES = flat(Service.values());

  public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
  }

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
    http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(authorize -> authorize
            // Permitir OPTIONS sin autenticación
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            // Acceso restringido
            .requestMatchers(GET, "/hotels*", "/hotels/**").authenticated()

            .requestMatchers(POST, "/hotels*")
            .hasAnyAuthority(flat(ADMIN, MANAGER))

            .requestMatchers(DELETE, "/hotels*")
            .hasAnyAuthority(anyService(ADMIN))

            .requestMatchers(DELETE, "/hotels/{id}")
            .hasAnyAuthority(flat(ADMIN, MANAGER))

            .requestMatchers(PATCH, "/hotels/{id}/rooms/{rid}")
            .hasAnyAuthority(flat(ADMIN, MANAGER))

            // Rechazar el resto
            .anyRequest().denyAll())
        // Registra el filtro antes del filtro estándar de autenticación
        .addFilterBefore(jwtAuthenticationFilter,
            UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
