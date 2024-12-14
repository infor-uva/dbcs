package com.uva.apis.bookings.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

public class ClientApi {

  @Autowired
  private RestTemplate restTemplate;

  @Value("${external.services.users.url}")
  private String USER_API_URL;

  public boolean existsById(int id) {
    String url = USER_API_URL + "/client/{id}";
    JsonNode response = restTemplate.getForEntity(url, JsonNode.class, id).getBody();
    return response.get("id").asInt(-1) == id;
  }

}
