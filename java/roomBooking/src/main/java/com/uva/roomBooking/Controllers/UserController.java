package com.uva.roomBooking.Controllers;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uva.roomBooking.Models.UserStatus;
import com.uva.roomBooking.Models.Booking;
import com.uva.roomBooking.Models.User;
import com.uva.roomBooking.Repositories.BookingRepository;
import com.uva.roomBooking.Repositories.UserRepository;

@RestController
@RequestMapping("users")
@CrossOrigin(origins = "*")
public class UserController {
  private final UserRepository userRepository;
  private final BookingRepository bookingRepository;

  public UserController(UserRepository userRepository, BookingRepository bookingRepository) {
    this.userRepository = userRepository;
    this.bookingRepository = bookingRepository;
  }

  @GetMapping
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  @PostMapping
  public User addUser(@RequestBody User user) {
    // TODO revisar como se desea manejar estado por defecto
    user.setStatus(UserStatus.NO_BOOKINGS);
    // Aunque se asegure a lo mejor no es la forma de manejo esperada
    return userRepository.save(user);
  }

  @GetMapping("/{id}")
  public User getUserById(@PathVariable int id) {
    return userRepository.findById(id).orElseThrow();
  }

  @PutMapping("/{id}")
  public User updateUserData(@PathVariable int id, @RequestBody Map<String, String> json) {
    User target = userRepository.findById(id).orElseThrow();
    if (!json.containsKey("name") || !json.containsKey("email")) {
      // TODO cambiar manejo
      throw new RuntimeException("Missing required fields");
    }
    target.setName(json.get("name"));
    target.setEmail(json.get("email"));
    return userRepository.save(target);
  }

  @PatchMapping("/{id}")
  public User updateUserState(@PathVariable int id, @RequestBody Map<String, String> json) {
    User target = userRepository.findById(id).orElseThrow();
    String strStatus = json.get("status");
    if (strStatus == null) {
      // TODO cambiar manejo
      throw new RuntimeException("Missing required fields");
    }
    UserStatus userStatus = UserStatus.valueOf(strStatus);
    switch (userStatus) {
      // TODO Buscar como validar las (in)active bookings
      case NO_BOOKINGS:
        if (!target.getBookings().isEmpty())
          throw new IllegalArgumentException("Invalid State: The user have at least one booking");
      case WITH_ACTIVE_BOOKINGS:
        if (target.getBookings().isEmpty())
          throw new IllegalArgumentException("Invalid State: The user don't have bookings");
      case WITH_INACTIVE_BOOKINGS:
        if (target.getBookings().isEmpty())
          throw new IllegalArgumentException("Invalid State: The user don't have bookings");
      default:
        break;
    }
    target.setStatus(userStatus);
    return userRepository.save(target);
  }

  @DeleteMapping("/{id}")
  public User deleteUser(@PathVariable Integer id) {
    User target;
    if ((target = userRepository.findById(id).orElseThrow()) != null) {
      userRepository.deleteById(id);
    }
    return target;
  }

  @GetMapping("/{id}/bookings")
  public List<Booking> getUserBookingsById(@PathVariable int id) {
    User user = userRepository.findById(id).orElseThrow();
    return bookingRepository.findByUserId(user);
  }
}
