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
import com.uva.roomBooking.Models.User;
import com.uva.roomBooking.Repositories.UserRepository;

@RestController
@RequestMapping("users")
@CrossOrigin(origins = "*")
public class UserController {
  private final UserRepository repository;

  public UserController(UserRepository repository) {
    this.repository = repository;
  }

  @GetMapping
  public List<User> getAllUsers() {
    return repository.findAll();
  }

  @PostMapping
  public User addUser(@RequestBody User user) {
    // TODO revisar como se desea manejar estado por defecto
    user.setStatus(UserStatus.NO_BOOKINGS);
    // Aunque se asegure a lo mejor no es la forma de manejo esperada
    return repository.save(user);
  }

  @GetMapping("/{id}")
  public User getUserById(@PathVariable int id) {
    return repository.findById(id).orElseThrow();
  }

  @PutMapping("/{id}")
  public User updateUserData(@PathVariable int id, @RequestBody Map<String, String> json) {
    User target = repository.findById(id).orElseThrow();
    if (!json.containsKey("name") || !json.containsKey("email")) {
      // TODO cambiar manejo
      throw new RuntimeException("Missing required fields");
    }
    target.setName(json.get("name"));
    target.setEmail(json.get("email"));
    return repository.save(target);
  }

  @PatchMapping("/{id}")
  public User updateUserState(@PathVariable int id, @RequestBody Map<String, String> json) {
    User target = repository.findById(id).orElseThrow();
    String strStatus = json.get("status");
    if (strStatus == null) {
      // TODO cambiar manejo
      throw new RuntimeException("Missing required fields");
    }
    UserStatus userStatus = UserStatus.valueOf(strStatus);
    switch (userStatus) {
      case NO_BOOKINGS:
        if (!target.getBookings().isEmpty())
          throw new IllegalArgumentException("Invalid State: The user have at least one booking");
      case WITH_ACTIVE_BOOKINGS:
      case WITH_INACTIVE_BOOKINGS:
        // TODO
      default:
        break;
    }
    target.setStatus(userStatus);
    return repository.save(target);
  }

  @DeleteMapping("/{id}")
  public User deleteUser(@PathVariable Integer id) {
    User target;
    if ((target = repository.findById(id).orElseThrow()) != null) {
      repository.deleteById(id);
    }
    return target;
  }
}
