package com.uva.api.auth.modules.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class SecurityUtils {

  private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  public static String encrypt(String value) {
    return encoder.encode(value);
  }

  // Método para comparar la contraseña ingresada con el hash almacenado
  public static boolean checkPassword(String rawPassword, String encodedPassword) {
    return encoder.matches(rawPassword, encodedPassword); // Comparar la contraseña con el hash
  }

}
