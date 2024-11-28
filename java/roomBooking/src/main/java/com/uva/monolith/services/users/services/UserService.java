package com.uva.monolith.services.users.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.uva.monolith.services.users.models.Client;
import com.uva.monolith.services.users.models.User;
import com.uva.monolith.services.users.models.UserStatus;
import com.uva.monolith.services.users.repositories.ClientRepository;
import com.uva.monolith.services.users.repositories.UserRepository;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ClientRepository clientRepository;

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  private User assertUser(Optional<? extends User> opUser) {
    return opUser.orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
  }

  public User getUserById(int id) {
    return assertUser(userRepository.findById(id));
  }

  public User getUserByEmail(String email) {
    return assertUser(userRepository.findByEmail(email));
  }

  public User addUser(User user) {
    // Actualmente está en el servicio AUTH
    // TODO adaptar adecuadamente
    throw new HttpClientErrorException(HttpStatus.MOVED_PERMANENTLY, "servicio actual en http://localhost:8101/login");
    // user.setStatus(UserStatus.NO_BOOKINGS);
    // if (user.getRol() == null) // Rol por defecto
    // user.setRol(UserRol.CONSUMER);
    // // Guardamos
    // return userRepository.save(user);
  }

  public User updateUserData(int id, String name, String email) {
    User user = getUserById(id);
    user.setName(name);
    user.setEmail(email);
    return userRepository.save(user);
  }

  public User updateUserStatus(int id, UserStatus status) {

    Client user = (Client) assertUser(clientRepository.findById(id));

    boolean activeBookings = user.getBookings().stream()
        .anyMatch(booking -> !booking.getEndDate().isBefore(LocalDate.now())); // reserva >= ahora
    boolean inactiveBookings = user.getBookings().stream()
        .anyMatch(booking -> booking.getStartDate().isBefore(LocalDate.now())); // reserva < ahora

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
    return userRepository.save(user);
  }

  public User deleteUserById(int id) {
    User user = getUserById(id);
    // TODO eliminar reservas de usuario ahora mismo no por el modo cascada pero a
    // futuro sí, después de la disgregación en microservicios
    userRepository.deleteById(id);
    return user;
  }
}
