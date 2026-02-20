package com.uva.common.jwt.core;

import java.util.Date;

public class JwtData {
  private String token;
  private int id;
  private String name;
  private String email;
  private UserRol rol;
  private Service service;
  private String subject;
  private String audience;
  private Long ttl;
  private Date issuedAt;
  private Date expiresAt;

  public JwtData() {
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

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

  public UserRol getRol() {
    return rol;
  }

  public void setRol(UserRol rol) {
    this.rol = rol;
  }

  public Service getService() {
    return service;
  }

  public void setService(Service service) {
    this.service = service;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getAudience() {
    return audience;
  }

  public void setAudience(String audience) {
    this.audience = audience;
  }

  public Long getTtl() {
    return ttl;
  }

  public void setTtl(Long ttl) {
    this.ttl = ttl;
  }

  public Date getIssuedAt() {
    return issuedAt;
  }

  public void setIssuedAt(Date issuedAt) {
    this.issuedAt = issuedAt;
  }

  public Date getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(Date expiresAt) {
    this.expiresAt = expiresAt;
  }

  public boolean isAdmin() {
    return rol != null && rol == UserRol.ADMIN;
  }
}
