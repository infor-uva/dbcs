// TODO eliminar si realmente no necesitamos comunicar un servicio con otro
package com.uva.authentication.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.uva.authentication.models.RegisterRequest;
import com.uva.authentication.models.User;
import com.uva.authentication.models.UserRol;
import com.uva.authentication.utils.JwtUtil;

@Component
public class UserAPI {

  @Autowired
  private RestTemplate restTemplate;

  @Value("${external.services.users.baseurl}")
  private String USER_API_URL;

  @Autowired
  private JwtUtil jwtUtil;

  private String token;
  private final User USER = new User(-1, "admin", null, null, UserRol.ADMIN);

  private String getAccessToken() {
    if (token == null || token.isEmpty() || jwtUtil.isTokenValid(token, USER)) {
      token = jwtUtil.generateToken(USER);
    }
    return token;
  }

  public User getUserByEmail(String email) {

    // Implementación para acceder con autentificación
    // String token = getAccessToken();
    // HttpHeaders headers = new HttpHeaders();
    // headers.set("Authorization", "Bearer " + token);
    // HttpEntity<Void> entity = new HttpEntity<>(headers);

    String url = USER_API_URL + "?email={" + email + "}";
    try {
      ResponseEntity<User> userResponse = restTemplate.getForEntity(url, User.class, email);
      // restTemplate.exchange(url, HttpMethod.GET, entity, User.class);
      return userResponse.getBody();
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() != HttpStatus.NOT_FOUND)
        throw e;
      return null;
    }
  }

  public User registerUser(RegisterRequest registerRequest) {

    String token = getAccessToken();
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token);

    String url = USER_API_URL;
    ResponseEntity<User> userResponse = restTemplate.postForEntity(url, registerRequest, User.class, headers);
    if (!userResponse.getStatusCode().is2xxSuccessful())
      throw new RuntimeException("Failed to register user");

    return userResponse.getBody();
  }

}
