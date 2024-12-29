package com.uva.api.bookings.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.uva.api.bookings.models.external.users.ClientDTO;
import com.uva.api.bookings.models.external.users.ClientStatus;

@Component
public class UserApi {

  @Autowired
  private RestTemplate restTemplate;

  @Value("${services.external.clients.url}")
  private String CLIENTS_API_URL;

  @Value("${services.external.managers.url}")
  private String MANAGERS_API_URL;

  public ClientDTO findClientById(int id) {
    try {
      String url = CLIENTS_API_URL + "/{id}";
      ClientDTO client = restTemplate.getForObject(url, ClientDTO.class, id);
      return client;
    } catch (HttpClientErrorException ex) {
      if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
        return null;
      }
      throw ex;
    }
  }

  public boolean existsManagerById(int id) {
    try {
      String url = MANAGERS_API_URL + "/{id}";
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

  public void updateClientState(int id, ClientStatus state) {
    String url = CLIENTS_API_URL + "/{id}";

    Map<String, Object> body = new HashMap<>();
    System.out.println(state);
    body.put("status", state);

    restTemplate.put(url, body, id);
  }

}
