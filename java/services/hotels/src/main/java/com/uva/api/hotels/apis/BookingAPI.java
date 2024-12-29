package com.uva.api.hotels.apis;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class BookingAPI {

  @Autowired
  private RestTemplate restTemplate;

  @Value("${services.external.bookings.url}")
  private String BOOKING_API_URL;

  public Set<Integer> getNotAvailableRooms(LocalDate start, LocalDate end) {
    String url = BOOKING_API_URL + "?start={start}&end={end}";
    Map<?, ?>[] bookingsArray = restTemplate
        .getForObject(url, Map[].class, start, end);
    Set<Integer> notAvailableRooms = new HashSet<>();
    for (Map<?, ?> booking : bookingsArray)
      notAvailableRooms.add((Integer) booking.get("roomId"));

    return notAvailableRooms;
  }

  public Set<Integer> getNotAvailableRooms(int hotelId, LocalDate start, LocalDate end) {
    String url = BOOKING_API_URL + "?hotelId={hotelId}&start={start}&end={end}";
    Map<?, ?>[] bookingsArray = restTemplate
        .getForObject(url, Map[].class, hotelId, start, end);
    Set<Integer> notAvailableRooms = new HashSet<>();
    for (Map<?, ?> booking : bookingsArray)
      notAvailableRooms.add((Integer) booking.get("roomId"));

    return notAvailableRooms;
  }

  public void deleteAllByHotelId(Integer id) {
    try {
      String url = BOOKING_API_URL + "?hotelId={id}";
      restTemplate.delete(url, id);
    } catch (HttpClientErrorException ex) {
      if (ex.getStatusCode() != HttpStatus.NOT_FOUND)
        throw ex;
    }
  }

  public void deleteAllByManagerId(Integer managerId) {
    try {
      String url = BOOKING_API_URL + "?managerId={managerId}";
      restTemplate.delete(url, managerId);
    } catch (HttpClientErrorException ex) {
      if (ex.getStatusCode() != HttpStatus.NOT_FOUND)
        throw ex;
    }
  }
}