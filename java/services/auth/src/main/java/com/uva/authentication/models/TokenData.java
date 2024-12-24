package com.uva.authentication.models;

import java.lang.reflect.Field;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class TokenData {
  private Integer id;
  private String name;
  private String email;
  private String rol;
  private String service;

  private String subject;
  private String audience;
  private Long ttl;

  public TokenData(DecodedJWT decoded, long ttl) {

    subject = decoded.getSubject();
    audience = decoded.getAudience().get(0);
    this.ttl = ttl;

    for (Field field : this.getClass().getDeclaredFields()) {
      field.setAccessible(true);

      // Verificamos si el campo está en el mapa y asignamos el valor
      Claim claim = decoded.getClaim(field.getName());
      if (!claim.isMissing()) {
        try {
          // Dependiendo del tipo de campo, asignamos el valor
          if (field.getType() == Integer.class) {
            field.set(this, Integer.parseInt(claim.asString()));
          } else if (field.getType() == String.class) {
            field.set(this, claim.asString());
          }
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public boolean isAdmin() {
    return rol != null && rol == "ADMIN";
  }
}