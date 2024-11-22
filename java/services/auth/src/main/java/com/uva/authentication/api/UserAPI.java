package com.uva.authentication.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.uva.authentication.models.RegisterRequest;
import com.uva.authentication.models.User;

@Component
public class UserAPI {

  @Autowired
  private RestTemplate restTemplate;

  @Value("${external.services.users.baseurl}")
  private String USER_API_URL;

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

  public User registerUser(RegisterRequest registerRequest) {
    String url = USER_API_URL;
    ResponseEntity<User> userResponse = restTemplate.postForEntity(url, registerRequest, User.class);
    if (!userResponse.getStatusCode().is2xxSuccessful())
      throw new RuntimeException("Failed to register user");

    return userResponse.getBody();
  }

}
