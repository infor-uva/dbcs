package com.uva.roomBooking.Controllers;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
  public List<User> getAllRooms() {
    return repository.findAll();
  }

  @PostMapping
  public User addRoom(@RequestBody User user) {
    return repository.save(user);
  }

  @DeleteMapping("/{id}")
  public User deleteRoom(@PathVariable Integer id) {
    User target;
    if ((target = repository.findById(id).orElse(null)) != null) {
      repository.deleteById(id);
    }
    return target;
  }
}
