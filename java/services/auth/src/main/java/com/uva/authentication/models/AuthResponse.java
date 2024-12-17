package com.uva.authentication.models;

import com.uva.authentication.models.remote.UserRol;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AuthResponse {
  private int id;
  private String name;
  private String email;
  private String password;
  private UserRol rol;
}
