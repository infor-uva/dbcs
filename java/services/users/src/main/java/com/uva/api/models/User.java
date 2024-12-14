package com.uva.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(nullable = false)
  private int id;

  @Basic(optional = false)
  @Column(nullable = false)
  private String name;

  @Basic(optional = false)
  @Column(nullable = false, unique = true)
  private String email;

  @Basic(optional = false)
  @Column(nullable = false)
  private String password;

  @Basic(optional = false)
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private UserRol rol = UserRol.CLIENT;

  public User() {
  }

  public User(int id, String name, String email, String password, UserRol rol) {
    setId(id);
    setName(name);
    setEmail(email);
    setRol(rol);
  }

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String rawPassword) {
    this.password = rawPassword;
  }

  public UserRol getRol() {
    return this.rol;
  }

  public void setRol(UserRol rol) {
    this.rol = rol;
  }
}
