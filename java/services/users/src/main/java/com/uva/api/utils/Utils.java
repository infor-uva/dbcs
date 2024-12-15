package com.uva.api.utils;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.uva.api.models.User;

public class Utils {
  public static <T extends User> T assertUser(Optional<T> opUser) {
    return opUser.orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
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
