package com.uva.api.services.users.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.uva.api.services.users.models.AuthResponse;
import com.uva.api.services.users.models.Client;
import com.uva.api.services.users.models.HotelManager;
import com.uva.api.services.users.models.User;
import com.uva.api.services.users.models.UserRol;
import com.uva.api.services.users.models.UserStatus;
import com.uva.api.services.users.repositories.ClientRepository;
import com.uva.api.services.users.repositories.HotelManagerRepository;
import com.uva.api.services.users.repositories.UserRepository;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ClientRepository clientRepository;

  @Autowired
  private HotelManagerRepository hotelManagerRepository;

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  private User assertUser(Optional<? extends User> opUser) {
    return opUser.orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
  }

  public User getUserById(int id) {
    return assertUser(userRepository.findById(id));
  }

  public AuthResponse getUserByEmail(String email) {
    User u = assertUser(userRepository.findByEmail(email));
    AuthResponse auth = new AuthResponse();
    BeanUtils.copyProperties(u, auth);
    return auth;
  }

  public User registerNewUser(User registerRequest) {
    User newUser;

    // Aseguramos que tenga un rol, por defecto es cliente
    if (registerRequest.getRol() == null)
      registerRequest.setRol(UserRol.CLIENT);

    switch (registerRequest.getRol()) {
      case HOTEL_ADMIN:
        HotelManager hm = new HotelManager();
        BeanUtils.copyProperties(registerRequest, hm);
        newUser = hotelManagerRepository.save(hm);
        break;

      case ADMIN:
        User admin = new User();
        BeanUtils.copyProperties(registerRequest, admin);
        newUser = admin; // userAPI.save(admin);
        break;

      case CLIENT: // Por defecto cliente normal
      default:
        Client client = new Client();
        BeanUtils.copyProperties(registerRequest, client);
        client.setRol(UserRol.CLIENT);
        newUser = clientRepository.save(client);
        break;
    }
    return newUser;
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
