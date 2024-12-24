package com.uva.authentication.models.remote;

import com.uva.authentication.models.auth.RegisterRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class User extends RegisterRequest {
  private int id;

  public User(int id, String email, String password, String name, UserRol rol) {
    super();
    this.id = id;
    setEmail(email);
    setName(name);
    setPassword(password);
    setRol(rol);
  }
}
