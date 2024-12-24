package com.uva.authentication.models.auth;

import com.uva.authentication.models.remote.UserRol;

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
