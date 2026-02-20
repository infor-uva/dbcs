package com.uva.api.auth.modules.auth;

import com.uva.api.auth.modules.user.UserEntity;
import com.uva.api.auth.modules.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public @NotNull UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    log.info("Loading user by email: {}", email);

    UserEntity user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

    return User
        .withUsername(user.getEmail())
        .password(user.getPassword() != null ? user.getPassword() : "")
        .authorities(List.of())
        .build();
  }
}
