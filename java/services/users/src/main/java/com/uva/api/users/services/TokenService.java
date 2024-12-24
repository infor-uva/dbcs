package com.uva.api.users.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uva.api.users.api.TokenAPI;
import com.uva.api.users.models.remote.JwtData;

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
      System.out.println("Generando token");
      long s = System.currentTimeMillis();
      ownToken = api.getServiceToken();
      long t = System.currentTimeMillis() - s;
      System.out.println("Token Generando en " + t + " ms");
    }
    return ownToken.getToken();
  }

  public JwtData decodeToken(String token) {
    if (cache.containsKey(token))
      return cache.get(token);
    System.out.println("Actualizando token");
    long s = System.currentTimeMillis();
    JwtData decoded = api.decodeToken(token);
    long t = System.currentTimeMillis() - s;
    System.out.println("Actualizando token en " + t + " ms");
    cache.put(token, decoded);
    return decoded;
  }

}
