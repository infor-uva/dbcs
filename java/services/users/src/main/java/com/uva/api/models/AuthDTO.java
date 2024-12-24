package com.uva.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AuthDTO {

  private int id;
  private String name;
  private String email;
  private String password;
  private UserRol rol = UserRol.CLIENT;
}
