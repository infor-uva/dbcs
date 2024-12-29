package com.uva.api.users.utils;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.uva.api.users.exceptions.UserNotFoundException;

public class Utils {
  public static <T> T assertUser(Optional<T> opUser) {
    return opUser.orElseThrow(() -> new UserNotFoundException());
  }

  public static String getToken(String authorization) {
    String prefix = "Bearer ";
    if (!authorization.startsWith(prefix))
      throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
    return authorization.substring(prefix.length());
  }

  public static boolean notEmptyStrings(String... values) {
    for (String value : values) {
      if (value == null || value.isEmpty()) {
        return false;
      }
    }
    return true;
  }
}
