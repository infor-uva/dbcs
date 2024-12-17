package com.uva.apis.bookings.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ClientApi {

  @Autowired
  private RestTemplate restTemplate;

  @Value("${external.services.clients.url}")
  private String CLIENTS_API_URL;

  public boolean existsById(int id) {
    String url = CLIENTS_API_URL + "/" + id;
    System.out.println(url);
    ResponseEntity<Void> response = restTemplate.getForEntity(url, Void.class, id);
    return response.getStatusCode() == HttpStatus.OK;
  }

}
