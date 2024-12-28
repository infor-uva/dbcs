package com.uva.api.users.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BookingAPI {

  @Autowired
  private RestTemplate restTemplate;

  @Value("${services.external.bookings.url}")
  private String BOOKING_API_URL;

  public void deleteAllByUserId(int id) {
    String url = BOOKING_API_URL + "?userId={id}";
    restTemplate.delete(url, id);
  }
}
