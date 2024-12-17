package com.uva.api.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uva.api.apis.BookingAPI;
import com.uva.api.models.Client;
import com.uva.api.models.User;
import com.uva.api.models.UserRol;
import com.uva.api.models.ClientStatus;
import com.uva.api.models.remote.Booking;
import com.uva.api.repositories.ClientRepository;
import com.uva.api.utils.Utils;

@Service
public class ClientService {

  @Autowired
  private ClientRepository clientRepository;

  @Autowired
  private BookingAPI bookingAPI;

  public List<Client> findAll() {
    return clientRepository.findAll();
  }

  public Client findById(int id) {
    Client client = Utils.assertUser(clientRepository.findById(id));
    List<Booking> bookings;
    try {
        bookings = bookingAPI.getAllBookingsByUserId(client.getId());
    } catch (Exception e) {
        bookings = new ArrayList<>(); 
    }
    client.setBookings(bookings);
    return client;
}



  public Client deleteById(int id) {
    Client client = Utils.assertUser(clientRepository.findById(id));
    clientRepository.delete(client);
    return client;
  }

  public Client save(User request) {
    Client client = new Client();
    BeanUtils.copyProperties(request, client);
    // Default rol
    client.setRol(UserRol.CLIENT);
    return clientRepository.save(client);
  }

  public User updateClientStatus(int id, ClientStatus status) {
    Client user = Utils.assertUser(clientRepository.findById(id));

    boolean activeBookings = user.getBookings().stream()
        .anyMatch(booking -> !booking.getEndDate().isBefore(LocalDate.now())); // reserva >= ahora
    boolean inactiveBookings = user.getBookings().stream()
        .anyMatch(booking -> booking.getEndDate().isBefore(LocalDate.now())); // reserva < ahora

    switch (status) {
      case NO_BOOKINGS:
        if (!user.getBookings().isEmpty())
          throw new IllegalArgumentException("Invalid State: The user has at least one booking");
        break;
      case WITH_ACTIVE_BOOKINGS:
        if (user.getBookings().isEmpty())
          throw new IllegalArgumentException("Invalid State: The user don't has bookings");
        if (!activeBookings)
          throw new IllegalArgumentException("Invalid State: The user don't has active bookings");
        break;
      case WITH_INACTIVE_BOOKINGS:
        if (user.getBookings().isEmpty())
          throw new IllegalArgumentException("Invalid State: The user don't has bookings");
        if (!inactiveBookings)
          throw new IllegalArgumentException("Invalid State: The user don't has inactive bookings");
        break;
      default:
        break;
    }

    user.setStatus(status);

    return clientRepository.save(user);
  }
}
