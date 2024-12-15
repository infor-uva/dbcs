package com.uva.monolith.api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class HotelManagerAPI {

  @Autowired
  private RestTemplate restTemplate;

  @Value("${external.services.managers.url}")
  private String MANAGERS_API_URL;

  public Boolean existsHotelManagerById(int id) {
    try {
      String url = MANAGERS_API_URL + "/{id}";
      return restTemplate.getForEntity(url, Map.class, id).getBody().containsKey("id");
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() != HttpStatus.NOT_FOUND)
        throw e;
      return false;
    }
  }

}
