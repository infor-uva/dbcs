package com.uva.api.bookings.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class UserApi {

  @Autowired
  private RestTemplate restTemplate;

  @Value("${services.external.users.url}")
  private String USERS_API_URL;

  public boolean existsClientById(int id) {
    try {
      String url = USERS_API_URL + "/clients/{id}";
      ResponseEntity<Void> response = restTemplate.getForEntity(url, Void.class, id);
      return response.getStatusCode() == HttpStatus.OK;
    } catch (HttpClientErrorException ex) {
      if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
        return false;
      }
      throw ex;
    }
  }

  public boolean existsManagerById(int id) {
    try {
      String url = USERS_API_URL + "/managers/{id}";
      ResponseEntity<Void> response = restTemplate.getForEntity(url, Void.class, id);
      return response.getStatusCode() == HttpStatus.OK;
    } catch (HttpClientErrorException ex) {
      System.out.println(ex.getStatusCode());
      if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
        return false;
      }
      throw ex;
    }
  }

}
