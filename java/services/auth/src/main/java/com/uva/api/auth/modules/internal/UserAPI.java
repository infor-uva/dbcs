package com.uva.api.auth.modules.internal;

import com.uva.api.auth.modules.auth.dto.RegisterRequest;
import com.uva.api.auth.modules.internal.dto.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserAPI {

  private final RestTemplate restTemplate;
  private final String userApiUrl;

  public UserAPI(
          RestTemplate restTemplate,
          @Value("${services.internal.users.url}") String userApiUrl
  ) {
    if (userApiUrl == null) {
      throw new IllegalArgumentException("userApiUrl cannot be null");
    }
    this.restTemplate = restTemplate;
    this.userApiUrl = userApiUrl;
  }


  /**
   * Get the user by email
   *
   * @param email
   * @return User or null if not exists
   * @throws HttpClientErrorException
   */
  public User getUserByEmail(String email) {
    String url = userApiUrl + "?email={email}";

    try {
      ResponseEntity<User> userResponse = restTemplate.getForEntity(url, User.class, email);
      return userResponse.getBody();
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() != HttpStatus.NOT_FOUND)
        throw e;
      return null;
    }
  }

  /**
   * Register the user if isn't register yet
   *
   * @param registerRequest
   * @return register result
   * @throws HttpClientErrorException
   * @throws HttpClientErrorException
   */
  public User registerUser(RegisterRequest registerRequest) {
    String url = userApiUrl;
    try {
      ResponseEntity<User> userResponse = restTemplate.postForEntity(url, registerRequest, User.class);
      return userResponse.getBody();
    } catch (HttpClientErrorException ex) {
      if (ex.getStatusCode() == HttpStatus.BAD_REQUEST)
        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Register failed");
      throw ex;
    }
  }

  /**
   * Update the user's password
   *
   * @param user
   * @param hashPass
   */
  public void changePassword(User user, String hashPass) {
    String url = userApiUrl + "/{id}/password";

    int id = user.id();

    Map<String, Object> body = new HashMap<>();
    body.put("password", hashPass);

    restTemplate.put(url, body, id);
  }

  public void deleteUser(int id) {
    String url = userApiUrl + "/{id}";

    restTemplate.delete(url, id);
  }

}
