package com.uva.api.users.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.uva.api.users.api.BookingAPI;
import com.uva.api.users.models.Client;
import com.uva.api.users.models.ClientStatus;
import com.uva.api.users.models.User;
import com.uva.api.users.models.UserRol;
import com.uva.api.users.models.remote.Booking;
import com.uva.api.users.repositories.ClientRepository;
import com.uva.api.users.utils.Utils;

@Service
public class ClientService {

  @Autowired
  private ClientRepository clientRepository;

  @Autowired
  private BookingAPI bookingAPI;

  public ResponseEntity<?> findAll() {
    return ResponseEntity.ok(clientRepository.findAll());
  }

  public ResponseEntity<?> findById(int id) {
    Client client = Utils.assertUser(clientRepository.findById(id));
    return ResponseEntity.ok(client);
  }

  public Client deleteById(int id) {
    Client client = Utils.assertUser(clientRepository.findById(id));
    bookingAPI.deleteAllByUserId(id);
    clientRepository.delete(client);
    return client;
  }

  public Client save(Client client) {
    // Default rol
    client.setRol(UserRol.CLIENT);
    return clientRepository.save(client);
  }

  // TODO No entiendo donde debería ir esto
  public User updateClientStatus(int id, ClientStatus status) {
    Client user = Utils.assertUser(clientRepository.findById(id));

    List<Booking> bookings = bookingAPI.getAllByUserId(id);

    boolean activeBookings = bookings.stream()
        .anyMatch(booking -> !booking.getEndDate().isBefore(LocalDate.now())); // reserva >= ahora
    boolean inactiveBookings = bookings.stream()
        .anyMatch(booking -> booking.getEndDate().isBefore(LocalDate.now())); // reserva < ahora

    switch (status) {
      case NO_BOOKINGS:
        if (!bookings.isEmpty())
          throw new IllegalArgumentException("Invalid State: The user has at least one booking");
        break;
      case WITH_ACTIVE_BOOKINGS:
        if (bookings.isEmpty())
          throw new IllegalArgumentException("Invalid State: The user don't has bookings");
        if (!activeBookings)
          throw new IllegalArgumentException("Invalid State: The user don't has active bookings");
        break;
      case WITH_INACTIVE_BOOKINGS:
        if (bookings.isEmpty())
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
