package com.uva.api.bookings.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class HotelApi {

  @Autowired
  private RestTemplate restTemplate;

  @Value("${services.external.hotels.url}")
  private String HOTEL_API_URL;

  public boolean existsById(int hotelId, int roomId) {
    String url = HOTEL_API_URL + "/{hotelId}/rooms/{roomId}";
    try {
      ResponseEntity<Void> response = restTemplate.getForEntity(url, Void.class, hotelId, roomId);
      return response.getStatusCode() == HttpStatus.OK;
    } catch (HttpClientErrorException ex) {
      if (ex.getStatusCode() == HttpStatus.NOT_FOUND)
        return false;
      throw ex;
    }
  }
}
