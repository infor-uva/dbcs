package com.uva.api.hotels.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class Utils {
  public static String getToken(String authorization) {
    String prefix = "Bearer ";
    if (!authorization.startsWith(prefix))
      throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
    return authorization.substring(prefix.length());
  }
}
