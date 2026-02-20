package com.uva.api.auth.modules.internal.dto;

// import com.uva.common.jwt.core.UserRol;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import org.springframework.lang.NonNull;

public record User(
  @Min(1) int id,
  @Email String email,
  @NonNull String password,
  @NonNull String name
//                @NonNull UserRol rol
) {
}
