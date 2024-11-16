package com.uva.users.Controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;

import com.uva.users.Models.UserStatus;
import com.uva.users.Models.User;
import com.uva.users.Repositories.UserRepository;

@RestController
@RequestMapping("users")
@CrossOrigin(origins = "*")

public class UserController {

  private UserRepository userRepository;
  @Autowired
  private RestTemplate restTemplate;

  @GetMapping
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  @PostMapping
  public User addUser(@RequestBody User user) {
    user.setStatus(UserStatus.NO_BOOKINGS);
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
      throw new RuntimeException("Missing required fields");
    }
    UserStatus userStatus = UserStatus.valueOf(strStatus);

    // Consultar el microservicio de booking
    ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
        "http://booking-service/users/{id}/bookings",
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<Map<String, Object>>>() {},
        id
    );
    List<Map<String, Object>> bookings = response.getBody();

    boolean activeBookings = bookings.stream()
        .anyMatch(booking -> LocalDate.parse((String) booking.get("endDate")).isAfter(LocalDate.now()));
    boolean inactiveBookings = bookings.stream()
        .anyMatch(booking -> LocalDate.parse((String) booking.get("startDate")).isBefore(LocalDate.now()));

    switch (userStatus) {
      case NO_BOOKINGS:
        if (!bookings.isEmpty())
          throw new IllegalArgumentException("Invalid State: The user has at least one booking");
        break;
      case WITH_ACTIVE_BOOKINGS:
        if (bookings.isEmpty() || !activeBookings)
          throw new IllegalArgumentException("Invalid State: The user doesn't have active bookings");
        break;
      case WITH_INACTIVE_BOOKINGS:
        if (bookings.isEmpty() || !inactiveBookings)
          throw new IllegalArgumentException("Invalid State: The user doesn't have inactive bookings");
        break;
      default:
        break;
    }
    target.setStatus(userStatus);
    return userRepository.save(target);
  }

  @DeleteMapping("/{id}")
  public User deleteUser(@PathVariable Integer id) {
    User target = userRepository.findById(id).orElseThrow();
    userRepository.deleteById(id);
    return target;
  }

  @GetMapping("/{id}/bookings")
  public List<Map<String, Object>> getUserBookingsById(@PathVariable int id) {
    // Llamada al microservicio de reservas
    ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
        "http://booking-service/users/{id}/bookings",
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<Map<String, Object>>>() {},
        id
    );
    return response.getBody();
  }
}
