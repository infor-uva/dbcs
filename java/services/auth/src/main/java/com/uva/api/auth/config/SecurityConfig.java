package com.uva.api.auth.config;

import com.uva.api.auth.modules.jwt.JwtKeyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Security configuration for the OAuth2 Authorization Server.
 * <p>
 * Features:
 * - STATELESS session management (no server-side sessions)
 * - CSRF disabled (RESTful API, not form-based)
 * - CORS configured for Angular frontend
 * - OAuth2 Authorization Server endpoints
 * - OAuth2 resource server endpoints
 * - Public endpoints: login, register, refresh, OAuth2 callbacks, JWKS
 * - Protected endpoints: user profile (requires JWT)
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
  private final JwtKeyManager jwtKeyManager;

  /**
   * Security filter chain for API endpoints.
   * Handles both OAuth2 Authorization Server and Resource Server roles.
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable()) // Stateless API, no CSRF needed
      .cors(Customizer.withDefaults()) // Enable CORS
      .sessionManagement(session -> session
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(auth -> auth
        // Public endpoints (no authentication required)
        .requestMatchers(
          "/auth/login",
          "/auth/register",
          "/auth/refresh",
          "/auth/logout",
          "/oauth2/**",
          "/login/oauth2/**",
          "/.well-known/**",
          "/actuator/health",
          "/actuator/health/liveness",
          "/actuator/health/readiness"
        ).permitAll()
        // Protected endpoints
        .requestMatchers("/auth/me").authenticated()
        // Deny everything else by default
        .anyRequest().denyAll()
      )
      // Form login (for browser-based OAuth2 provider redirects)
      .formLogin(Customizer.withDefaults())
      // OAuth2 login (GitHub, Google)
      .oauth2Login(oauth2 -> oauth2
        .successHandler(oAuth2LoginSuccessHandler)
      )
      // OAuth2 Resource Server (validate JWT tokens)
      .oauth2ResourceServer(oauth2 -> oauth2
        .jwt(jwt -> jwt.decoder(new org.springframework.security.oauth2.jwt.NimbusJwtDecoder(
          jwtKeyManager.getJwkSourceForValidation()
        )))
      );

    return http.build();
  }

  /**
   * CORS configuration: Allow Angular frontend.
   * Adjust origins for your environment.
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    final CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList(
      "http://localhost:4200",      // Local development
      "http://localhost:3000",       // Alternative dev port
      System.getenv("CORS_ORIGINS") != null ? System.getenv("CORS_ORIGINS") : ""
    ));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);

    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  /**
   * Password encoder: BCrypt for local user passwords.
   * Use Argon2 for production (more secure against GPU attacks).
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    // TODO: Upgrade to Argon2PasswordEncoder for production
    return new BCryptPasswordEncoder(12); // Strength 12 = good balance of security/speed
  }
}

