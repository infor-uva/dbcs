package com.uva.api.users.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.JsonNode;

import com.uva.api.users.models.AuthDTO;
import com.uva.api.users.services.UserService;
import com.uva.api.users.utils.Utils;

@RestController
@RequestMapping("users")
@CrossOrigin(origins = "*")
public class UserController {

  @Autowired
  private UserService userService;

  @PostMapping
  public ResponseEntity<?> addUser(@RequestBody AuthDTO body) {
    return userService.registerNewUser(body);
  }

  @PutMapping("/{id:\\d+}")
  public ResponseEntity<?> updateUserData(
      @RequestHeader(value = "Authorization", required = true) String authorization,
      @PathVariable int id, @RequestBody Map<String, String> json) {
    String token = Utils.getToken(authorization);
    String name = json.get("name");
    String email = json.get("email");

    if (!Utils.notEmptyStrings(name, email))
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing required fields");

    return userService.updateUserData(token, id, name, email);
  }

  @PutMapping("/{id:\\d+}/password")
  public ResponseEntity<?> updatePassword(
      @RequestHeader(value = "Authorization", required = true) String authorization,
      @PathVariable int id, @RequestBody JsonNode json) {
    String password = json.get("password").asText();
    String token = Utils.getToken(authorization);

    if (!Utils.notEmptyStrings(token, password))
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing required fields");

    return userService.changePassword(token, id, password);
  }

  @GetMapping(params = { "email" })
  public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
    return userService.getUserByEmail(email);
  }

  @GetMapping
  public ResponseEntity<?> getAllUsers() {
    return userService.getAllUsers();
  }

  @GetMapping("/{id:\\d+}")
  public ResponseEntity<?> getUserById(
      @RequestHeader(value = "Authorization", required = true) String authorization,
      @PathVariable int id) {
    String token = Utils.getToken(authorization);
    return userService.getUserById(token, id);
  }

  @DeleteMapping("/{id:\\d+}")
  public ResponseEntity<?> deleteUser(
      @RequestHeader(value = "Authorization", required = true) String authorization,
      @PathVariable int id) {
    String token = Utils.getToken(authorization);
    return userService.deleteUserById(token, id);
  }
}
