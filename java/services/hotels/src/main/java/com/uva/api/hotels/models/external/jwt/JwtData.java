package com.uva.api.hotels.models.external.jwt;

import java.util.Date;

import com.uva.api.hotels.models.external.users.UserRol;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Data
@ToString
public class JwtData {

  private String token;

  private int id = -1;
  private String name;
  private String email;
  private UserRol rol;
  private Service service;

  private String subject;
  private String audience;
  private Long ttl;

  private Date issuedAt;
  private Date expiresAt;

  public boolean isAdmin() {
    return rol != null && rol == UserRol.ADMIN;
  }
}