package com.uva.api.auth.models.auth;

import com.uva.api.auth.models.remote.UserRol;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RegisterRequest extends LoginRequest {
  private UserRol rol;
  private String name;
}
