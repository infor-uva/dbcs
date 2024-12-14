package com.uva.apis.bookings.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

public class HotelApi {

  @Autowired
  private RestTemplate restTemplate;

  @Value("${external.services.hotels.url}")
  private String HOTEL_API_URL;

  public boolean existsById(int hotelId, int roomId) {
    try {
      String url = HOTEL_API_URL + "/{hotelId}/rooms/{roomId}";
      JsonNode response = restTemplate.getForEntity(url, JsonNode.class, hotelId, roomId).getBody();
      return response.get("id").asInt(-1) == roomId;
    } catch (Exception e) {
      // TODO: disminuir el alcance
      return false;
    }
  }
}
