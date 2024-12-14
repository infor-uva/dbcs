package com.uva.monolith.services.users.models;

public class AuthResponse {

  private int id;
  private String username;
  private String email;
  private String password;
  private UserRol rol;

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public UserRol getRol() {
    return this.rol;
  }

  public void setRol(UserRol rol) {
    this.rol = rol;
  }

}
