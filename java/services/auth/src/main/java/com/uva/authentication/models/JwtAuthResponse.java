package com.uva.authentication.models;

public class JwtAuthResponse {
  private String token;

  public JwtAuthResponse(String token) {
    this.token = token;
  }

  // Getter
  public String getToken() {
    return token;
  }
}
