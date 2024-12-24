package com.uva.api.users.utils;

import java.util.Optional;

import com.uva.api.users.exceptions.UserNotFoundException;
import com.uva.api.users.models.User;

public class Utils {
  public static <T extends User> T assertUser(Optional<T> opUser) {
    return opUser.orElseThrow(() -> new UserNotFoundException());
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
