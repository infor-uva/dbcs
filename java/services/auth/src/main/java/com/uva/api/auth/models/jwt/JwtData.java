package com.uva.api.auth.models.jwt;

import java.lang.reflect.Field;
import java.util.Date;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.uva.api.auth.models.remote.UserRol;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Data
@ToString
public class JwtData {
  private Integer id;
  private String name;
  private String email;
  private UserRol rol;
  private String service;

  private String subject;
  private String audience;
  private Long ttl;

  private Date issuedAt;
  private Date expiresAt;

  public JwtData(DecodedJWT decoded, long ttl) {
    for (Field field : this.getClass().getDeclaredFields()) {
      field.setAccessible(true);

      // Verificamos si el campo está en el mapa y asignamos el valor
      Claim claim = decoded.getClaim(field.getName());
      if (!claim.isNull()) {
        String value = claim.asString();
        try {
          // Dependiendo del tipo de campo, asignamos el valor
          if (field.getType() == Integer.class) {
            field.set(this, claim.asInt());
          } else if (field.getType() == String.class) {
            field.set(this, claim.asString());
          } else if (field.getType() == UserRol.class) {
            if (value != null)
              field.set(this, UserRol.valueOf(value));
          }
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }

    }

    if (decoded.getAudience() != null && !decoded.getAudience().isEmpty())
      audience = decoded.getAudience().get(0);

    this.ttl = ttl;
    issuedAt = decoded.getIssuedAt();
    expiresAt = decoded.getExpiresAt();

    System.out.println("\nDECODED TOKEN: " + this + "\n");
  }

  public boolean isAdmin() {
    return rol != null && rol == UserRol.ADMIN;
  }
}