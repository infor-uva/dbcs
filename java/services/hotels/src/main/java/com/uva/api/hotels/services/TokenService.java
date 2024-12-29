package com.uva.api.hotels.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.uva.api.hotels.apis.TokenAPI;
import com.uva.api.hotels.models.external.jwt.JwtData;
import com.uva.api.hotels.models.external.users.UserRol;

@Service
public class TokenService {

  @Autowired
  private TokenAPI api;

  private JwtData ownToken;
  private Map<String, JwtData> cache = new HashMap<>();

  private boolean expireSoon(JwtData decoded) {
    return (decoded.getExpiresAt().getTime() - System.currentTimeMillis()) / 1000 <= 10;
  }

  public String getServiceToken() {
    if (ownToken == null || expireSoon(ownToken)) {
      System.out.println("\nGenerando token");
      long s = System.currentTimeMillis();
      ownToken = api.getServiceToken();
      long t = System.currentTimeMillis() - s;
      System.out.println("Token Generando en " + t + " ms\n");
    }
    return ownToken.getToken();
  }

  public JwtData decodeToken(String token) {
    JwtData decoded;
    if (cache.containsKey(token)) {
      decoded = cache.get(token);
      if (!expireSoon(decoded))
        return cache.get(token);
    }
    System.out.println("\nActualizando token");
    long s = System.currentTimeMillis();
    decoded = api.decodeToken(token);
    long t = System.currentTimeMillis() - s;
    System.out.println("Actualizando token en " + t + " ms\n");
    cache.put(token, decoded);
    return decoded;
  }

  /**
   * Valida que la entidad representada con el token tenga permisos de
   * administrador, sea un servicio o sea el dueño del recurso (idExpected)
   * 
   * @param token
   * @param idExpected
   */
  public void assertPermission(String token, int idExpected) {
    JwtData decoded = decodeToken(token);
    boolean isOwner = decoded.getId() == idExpected;
    if (!isOwner)
      assertPermission(token);
  }

  /**
   * Valida que la entidad representada con el token tenga permisos de
   * administrador o sea un servicio
   * 
   * @param token
   */
  public void assertPermission(String token) {
    JwtData decoded = decodeToken(token);
    boolean isAdmin = decoded.isAdmin();
    boolean isService = decoded.getService() != null && decoded.getAudience().equals("INTERNAL");
    if (!isAdmin && !isService)
      throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
  }

  public boolean hasAnyRole(String token, UserRol... roles) {
    JwtData decoded = decodeToken(token);
    for (UserRol role : roles)
      if (decoded.getRol() == role)
        return true;
    return false;
  }

}
