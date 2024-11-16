package com.uva.users.Models;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  private int id;

  @Basic(optional = false)
  private String name;

  @Basic(optional = false)
  private String email;

  @Basic(optional = false)
  @Enumerated(EnumType.STRING)
  private UserStatus status = UserStatus.NO_BOOKINGS;

  @Basic (optional = false)
  private String password;

  public User() {
  }

  public User(int id, String name, String email, UserStatus status, String password) {
    setId(id);
    setName(name);
    setEmail(email);
    setStatus(status);
    setPassword(password);
  }

  // Getters y setters
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
