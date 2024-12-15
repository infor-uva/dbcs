package com.uva.apis.bookings.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class HotelApi {

  @Autowired
  private RestTemplate restTemplate;

  @Value("${external.services.hotels.url}")
  private String HOTEL_API_URL;

  public boolean existsById(int hotelId, int roomId) {
    String url = HOTEL_API_URL + "/{hotelId}/rooms/{roomId}";
    ResponseEntity<Void> response = restTemplate.getForEntity(url, Void.class, hotelId, roomId);
    return response.getStatusCode() == HttpStatus.OK;
  }
}
