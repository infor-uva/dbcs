package com.uva.authentication.models;

public class User extends RegisterRequest {
  private int id;
  private UserStatus status;

  public User() {
  }

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public UserStatus getStatus() {
    return this.status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }
}
