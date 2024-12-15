package com.uva.apis.bookings.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class ClientApi {

  @Autowired
  private RestTemplate restTemplate;

  @Value("${external.services.users.url}")
  private String USER_API_URL;

  public boolean existsById(int id) {
    String url = USER_API_URL + "/client/{id}";
    ResponseEntity<Void> response = restTemplate.getForEntity(url, Void.class, id);
    return response.getStatusCode() == HttpStatus.OK;
  }

}
