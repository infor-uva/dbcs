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

  @PutMapping("/{id}")
  public ResponseEntity<?> updateUserData(@PathVariable int id, @RequestBody Map<String, String> json) {

    String name = json.get("name");
    String email = json.get("email");

    if (!Utils.notEmptyStrings(name, email))
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing required fields");

    return userService.updateUserData(id, name, email);
  }

  @PutMapping("/{id}/password")
  public ResponseEntity<?> updatePassword(@PathVariable int id, @RequestBody JsonNode json) {
    String password = json.get("password").asText();

    if (!Utils.notEmptyStrings(password))
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing required fields");

    return userService.changePassword(id, password);
  }

  // TODO aplicarr verificación
  // @Autowired
  // private TokenService ser;

  // private String validate(String token) {
  // JWTData decoded = ser.decodeToken(token);
  // if (decoded == null) {
  // return "Invalid token format";
  // }
  // UserRol rol = decoded.getRol();
  // String audience = decoded.getAudience();
  // boolean a = (rol == null || rol != UserRol.ADMIN);
  // boolean b = (audience == null || !audience.equals("INTERNAL"));
  // if (a && b) {
  // return "Invalid " + a + " " + b;
  // }
  // return null;

  // }

  // @GetMapping(params = { "email" })
  // public ResponseEntity<?> getUserByEmail(@RequestParam String email,
  // @RequestHeader(value = "Authorization", required = true) String
  // authorization) {
  // try {
  // if (authorization == null) {
  // return new ResponseEntity<String>("Missing required fields",
  // HttpStatus.BAD_REQUEST);
  // }
  // String m = validate(authorization.substring(7));
  // if (m != null) {
  // return new ResponseEntity<String>(m, HttpStatus.BAD_REQUEST);
  // }
  // return ResponseEntity.ok(userService.getUserByEmail(email));
  // } catch (HttpClientErrorException e) {
  // if (e.getStatusCode() == HttpStatus.NOT_FOUND)
  // return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
  // throw e;
  // }
  // }

  @GetMapping(params = { "email" })
  public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
    return userService.getUserByEmail(email);
  }

  @GetMapping
  public ResponseEntity<?> getAllUsers() {
    return userService.getAllUsers();
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getUserById(@PathVariable int id) {
    return userService.getUserById(id);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteUser(@PathVariable int id) {
    return userService.deleteUserById(id);
  }
}
