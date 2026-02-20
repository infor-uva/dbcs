package com.uva.api.auth.modules.auth;

import com.uva.api.auth.modules.auth.dto.LoginRequest;
import com.uva.api.auth.modules.auth.dto.RegisterRequest;
import com.uva.api.auth.modules.jwt.TokenService;
import com.uva.api.auth.modules.jwt.dto.TokenPairResponse;
import com.uva.api.auth.modules.user.AuthProvider;
import com.uva.api.auth.modules.user.UserEntity;
import com.uva.api.auth.modules.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  private final TokenService tokenService;

  /**
   * Authenticate with email + password and return JWT token pair.
   */
  public TokenPairResponse login(LoginRequest request) {
    UserEntity user = userRepository.findByEmail(request.email())
        .orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

    if (user.getPassword() == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
      throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }

    return tokenService.issueTokenPair(user);
  }

  /**
   * Register a new local user and return JWT token pair.
   */
  public TokenPairResponse register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new HttpClientErrorException(HttpStatus.CONFLICT, "Email already registered");
    }

    UserEntity user = UserEntity.builder()
        .email(request.email())
        .password(passwordEncoder.encode(request.password()))
        .name(request.name())
        .rol(request.rol())
        .provider(AuthProvider.LOCAL)
        .build();

    userRepository.save(user);

    return tokenService.issueTokenPair(user);
  }
}
