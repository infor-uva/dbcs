package com.uva.api.apis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HotelApi {

  @Autowired
  private RestTemplate restTemplate;

  @Value("${external.services.hotels.url}")
  private String HOTELS_API;

  public void deleteAllByManagerId(Integer id) {
    String url = HOTELS_API + "?managerId={id}";
    restTemplate.delete(url, id);
  }

}
