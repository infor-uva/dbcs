package com.uva.api.users.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HotelApi {

  @Autowired
  @Qualifier("IdentifyRestTemplate")
  private RestTemplate restTemplate;

  @Value("${services.external.hotels.url}")
  private String HOTELS_API;

  public void deleteAllByManagerId(Integer id) {
    String url = HOTELS_API + "?managerId={id}";
    restTemplate.delete(url, id);
  }
}
