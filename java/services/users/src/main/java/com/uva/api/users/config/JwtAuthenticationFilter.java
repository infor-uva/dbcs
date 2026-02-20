package com.uva.api.users.config;

import com.uva.api.users.models.remote.jwt.JwtData;
import com.uva.api.users.services.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.yaml.snakeyaml.util.Tuple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final TokenService service;

  public JwtAuthenticationFilter(TokenService service) {
    this.service = service;
  }

  private @NonNull @NotBlank Optional<String> getTokenFromRequest(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    return Optional.ofNullable(
            authHeader != null && authHeader.startsWith("Barear ")
            ? authHeader.substring(7)
            : null
    );
  }

  private JwtData validateAndDecodeToken(String token) {
    // TODO review the reason for this catch, there is no reason for return a null value in this case, if fails
    //  should throw a exception to handle the correct response
    try {
      return service.decodeToken(token);
    } catch (Exception ex) {
      log.error("Validation for token {} failed", token);
      log.debug("Validation token exception", ex);
      throw ex;
    }
  }

  private enum UserType {
    USER,
    SERVICE,
    UNKNOWN
  }

  @Override
  protected void doFilterInternal(
          @NotNull HttpServletRequest request,
          @NotNull HttpServletResponse response,
          @NotNull FilterChain filterChain
  ) throws ServletException, IOException {
    Optional<String> opToken = getTokenFromRequest(request);
    if (opToken.isEmpty()) {
      // TODO Stop filter and return 401 unauthorized
      log.info("No token provided for request {} {}", request.getMethod(), request.getRequestURI());
      return;
    }

    String token = opToken.get();
    log.info("Attempt {} {} with token {}", request.getMethod(), request.getRequestURI(), token);

    JwtData jwt = validateAndDecodeToken(token);
    log.debug("Decoding token {}, content: {}", token, jwt);

    var tuple = getUserType(jwt);
    UserType userType = tuple._1();
    List<SimpleGrantedAuthority> authorities = tuple._2();

    if (userType == UserType.UNKNOWN) {
      // TODO improve this
      log.warn("No identity found {}", jwt);
      return;
    }

    String email = jwt.getEmail();
    log.info("Successfully validated token {} ({} roles={} identity={} audience={}",
            token, email, jwt.getRol(), userType, authorities);

    if (SecurityContextHolder.getContext().getAuthentication() == null) {

      // Crear autenticación
      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
              email, null, authorities);
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

      // Establecer autenticación en el contexto de seguridad
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // Continuar con el resto de filtros
    filterChain.doFilter(request, response);
  }

  private Tuple<UserType, List<SimpleGrantedAuthority>> getUserType(@NotNull JwtData jwt) {
    UserType userType;
    String audience = jwt.getAudience();
    List<String> authorities = new ArrayList<>();

    if (audience.equals("INTERNAL") && service != null) {
      authorities.add(service.toString());
      userType = UserType.SERVICE;
    } else if (audience.equals("EXTERNAL") && jwt.getRol() != null) {
      authorities.add("ROLE_".concat(jwt.getRol().toString()));
      userType = UserType.USER;
    } else {
      userType = UserType.UNKNOWN;
    }

    return new Tuple<>(userType, authorities.stream().map(SimpleGrantedAuthority::new).toList());
  }
}
