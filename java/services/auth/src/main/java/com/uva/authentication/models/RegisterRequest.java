package com.uva.authentication.models;

public class RegisterRequest extends LoginRequest {
  private UserRol rol;
  private String name;

  public UserRol getRol() {
    return this.rol;
  }

  public void setRol(UserRol rol) {
    this.rol = rol;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
