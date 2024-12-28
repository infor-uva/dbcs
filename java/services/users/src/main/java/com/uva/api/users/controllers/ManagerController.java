package com.uva.api.users.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uva.api.users.services.ManagerService;
import com.uva.api.users.utils.Utils;

@RestController
@RequestMapping("users/managers")
@CrossOrigin(origins = "*")
public class ManagerController {

  @Autowired
  private ManagerService managerService;

  @GetMapping
  public ResponseEntity<?> getAllHotelManagers() {
    return managerService.findAll();
  }

  @GetMapping("/{id:\\d+}")
  public ResponseEntity<?> getHotelManagerById(
      @RequestHeader(value = "Authorization", required = true) String authorization,
      @PathVariable int id) {
    String token = Utils.getToken(authorization);
    return managerService.findById(token, id);
  }

  @DeleteMapping("/{id:\\d+}")
  public ResponseEntity<?> deleteHotelManager(
      @RequestHeader(value = "Authorization", required = true) String authorization,
      @PathVariable int id) {
    String token = Utils.getToken(authorization);
    return managerService.deleteById(token, id);
  }

}
