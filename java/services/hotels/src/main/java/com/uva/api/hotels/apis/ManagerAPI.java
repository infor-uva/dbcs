package com.uva.api.hotels.apis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class ManagerAPI {

  @Autowired
  private RestTemplate restTemplate;

  @Value("${services.external.managers.url}")
  private String MANAGERS_API_URL;

  public Boolean existsManagerById(int id) {
    try {
      String url = MANAGERS_API_URL + "/{id}";
      return restTemplate.getForEntity(url, JsonNode.class, id).getStatusCode() == HttpStatus.OK;
    } catch (HttpClientErrorException e) {
      e.printStackTrace(System.err);
      if (e.getStatusCode() != HttpStatus.NOT_FOUND)
        throw e;
      return false;
    }
  }

}
