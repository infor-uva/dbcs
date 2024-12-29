package com.uva.api.auth.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.uva.api.auth.models.auth.RegisterRequest;
import com.uva.api.auth.models.remote.User;

@Component
public class UserAPI {

  private final RestTemplate restTemplate;

  public UserAPI(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Value("${services.external.users.url}")
  private String USER_API_URL;

  /**
   * Get the user by email
   *
   * @param email
   * @return User or null if not exists
   * @throws HttpClientErrorException
   */
  public User getUserByEmail(String email) {
    String url = USER_API_URL + "?email={email}";

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
    String url = USER_API_URL;
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
    String url = USER_API_URL + "/{id}/password";

    int id = user.getId();

    Map<String, Object> body = new HashMap<>();
    body.put("password", hashPass);

    restTemplate.put(url, body, id);
  }

  public void deleteUser(int id) {
    String url = USER_API_URL + "/{id}";

    restTemplate.delete(url, id);
  }

}
