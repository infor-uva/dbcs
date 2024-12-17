package com.uva.api.apis;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.uva.api.models.remote.Booking;

@Component
public class BookingAPI {

  @Autowired
  private RestTemplate restTemplate;

  @Value("${external.services.bookings.url}")
  private String BOOKING_API_URL;

  public List<Booking> getAllBookingsByUserId(int id) {
    String url = BOOKING_API_URL + "?userId={id}";

    Booking[] bookingsArray = restTemplate
        .getForObject(url, Booking[].class, id);

    return Arrays.asList(bookingsArray);

  }

}
