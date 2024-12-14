package com.uva.api.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.uva.api.models.User;
import com.uva.api.models.UserStatus;
import com.uva.api.services.UserService;

@RestController
@RequestMapping("users")
@CrossOrigin(origins = "*")
public class UserController {

  @Autowired
  private UserService userService;

  @GetMapping
  public ResponseEntity<List<User>> getAllUsers() {
    List<User> users = userService.getAllUsers();
    return ResponseEntity.ok(users);
  }

  @GetMapping(params = { "email" })
  public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
    try {
      return ResponseEntity.ok(userService.getUserByEmail(email));
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND)
        return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
      throw e;
    }
  }

  @PostMapping
  public ResponseEntity<?> addUser(@RequestBody User user) {
    userService.registerNewUser(user);
    return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getUserById(@PathVariable int id) {
    return ResponseEntity.ok(userService.getUserById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateUserData(@PathVariable int id, @RequestBody Map<String, String> json) {
    System.err.println(json.entrySet().size());
    json.keySet().forEach(k -> System.err.println(k));
    String name = json.get("name");
    String email = json.get("email");
    if (name == null || email == null) {
      return new ResponseEntity<String>("Missing required fields", HttpStatus.BAD_REQUEST);
    }
    try {
      User user = userService.updateUserData(id, name, email);
      return new ResponseEntity<User>(user, HttpStatus.OK);
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND)
        return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
      throw e;
    }
  }

  @PatchMapping("/{id}")
  public ResponseEntity<?> updateUserState(@PathVariable int id, @RequestBody Map<String, String> json) {

    String strStatus = json.get("status");
    if (strStatus == null) {
      return new ResponseEntity<String>("Missing required fields", HttpStatus.BAD_REQUEST);
    }
    try {
      UserStatus userStatus = UserStatus.valueOf(strStatus);
      return ResponseEntity.ok(userService.updateUserStatus(id, userStatus));
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<String>("Unknown user state", HttpStatus.BAD_REQUEST);
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND)
        return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
      throw e;
    }

  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
    try {
      return ResponseEntity.ok(userService.deleteUserById(id));
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND)
        return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
      throw e;
    }
  }

}
