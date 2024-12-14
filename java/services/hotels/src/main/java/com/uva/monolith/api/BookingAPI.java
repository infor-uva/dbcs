package com.uva.monolith.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


@Component
public class BookingAPI {

  @Autowired
  private RestTemplate restTemplate;

  @Value("${external.services.users.url}")
  private String BOOKING_API_URL;

public void deleteAllByHotelId(Integer id) {
    try {
      String url = BOOKING_API_URL + "?hotelId={id}";
      restTemplate.delete(url, id);
    } catch (HttpClientErrorException e) {
        throw e;
    }
}



}




//restTemplate.getForEntity(url, User.class, email)