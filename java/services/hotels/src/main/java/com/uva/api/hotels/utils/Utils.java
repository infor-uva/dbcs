package com.uva.api.hotels.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class Utils {
  public static String getTokenSoft(String authorization) {
    String prefix = "Bearer ";
    return authorization != null && authorization.startsWith(prefix)
        ? authorization.substring(prefix.length())
        : null;
  }

  public static String getToken(String authorization) {
    String token = getTokenSoft(authorization);
    if (token == null)
      throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
    return token;
  }
}
