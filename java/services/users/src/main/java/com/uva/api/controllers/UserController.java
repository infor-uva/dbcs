package com.uva.api.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.uva.api.models.AuthResponse;
import com.uva.api.models.Client;
import com.uva.api.models.Manager;
import com.uva.api.models.User;
import com.uva.api.models.ClientStatus;
import com.uva.api.services.ClientService;
import com.uva.api.services.ManagerService;
import com.uva.api.services.UserService;
import com.uva.api.utils.Utils;

@RestController
@RequestMapping("users")
@CrossOrigin(origins = "*")
public class UserController {

  @Autowired
  private UserService userService;

  @Autowired
  private ClientService clientService;

  @Autowired
  private ManagerService managerService;

  // Common
  @PostMapping
  public ResponseEntity<?> addUser(@RequestBody AuthResponse body) {
    User user = new User();
    BeanUtils.copyProperties(body, user);
    userService.registerNewUser(user);
    return new ResponseEntity<User>(user, HttpStatus.ACCEPTED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateUserData(@PathVariable int id, @RequestBody Map<String, String> json) {

    String name = json.get("name");
    String email = json.get("email");

    if (!Utils.notEmptyStrings(name, email)) {
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

  @PutMapping("/{id}/password")
  public ResponseEntity<?> updatePassword(@PathVariable int id, @RequestBody JsonNode json) {
    String password = json.get("password").asText();

    if (!Utils.notEmptyStrings(password)) {
      return new ResponseEntity<String>("Missing required fields", HttpStatus.BAD_REQUEST);
    }

    try {
      User user = userService.changePassword(id, password);
      return new ResponseEntity<User>(user, HttpStatus.OK);
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND)
        return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
      throw e;
    }
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

  @GetMapping
  public ResponseEntity<List<User>> getAllUsers() {
    List<User> users = userService.getAllUsers();
    return ResponseEntity.ok(users);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getUserById(@PathVariable int id) {
    return ResponseEntity.ok(userService.getUserById(id));
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

  // Clients
  @GetMapping("/clients")
  public ResponseEntity<List<Client>> getAllClients() {
    List<Client> users = clientService.findAll();
    return ResponseEntity.ok(users);
  }

  @GetMapping("/clients/{id}")
  public ResponseEntity<Client> getClientById(@PathVariable int id) {
    return ResponseEntity.ok(clientService.findById(id));
  }

  @PatchMapping("/clients/{id}")
  public ResponseEntity<?> updateClientState(@PathVariable int id, @RequestBody Map<String, String> json) {

    String strStatus = json.get("status");
    if (strStatus == null) {
      return new ResponseEntity<String>("Missing required fields", HttpStatus.BAD_REQUEST);
    }
    try {
      ClientStatus clientStatus = ClientStatus.valueOf(strStatus);
      return ResponseEntity.ok(clientService.updateClientStatus(id, clientStatus));
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<String>("Unknown Client state", HttpStatus.BAD_REQUEST);
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND)
        return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
      throw e;
    }
  }

  @DeleteMapping("/clients/{id}")
  public ResponseEntity<?> deleteClient(@PathVariable Integer id) {
    try {
      return ResponseEntity.ok(clientService.deleteById(id));
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND)
        return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
      throw e;
    }
  }

  // HotelManagers
  @GetMapping("/managers")
  public ResponseEntity<List<Manager>> getAllHotelManagers() {
    List<Manager> users = managerService.findAll();
    return ResponseEntity.ok(users);
  }

  @GetMapping("/managers/{id}")
  public ResponseEntity<Manager> getHotelManagerById(@PathVariable int id) {
    return ResponseEntity.ok(managerService.findById(id));
  }

  @DeleteMapping("/managers/{id}")
  public ResponseEntity<?> deleteHotelManager(@PathVariable Integer id) {
    try {
      return ResponseEntity.ok(managerService.deleteById(id));
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND)
        return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
      throw e;
    }
  }

}
