package com.uva.authentication.models;

import java.util.Objects;

public class LoginRequest {
  private String email;
  private String password = String.valueOf(Objects.hashCode("hi"));

  // Getters and setters
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

}
