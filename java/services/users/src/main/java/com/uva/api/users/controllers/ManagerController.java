package com.uva.api.users.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uva.api.users.services.ManagerService;

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

  @GetMapping("/{id}")
  public ResponseEntity<?> getHotelManagerById(@PathVariable Integer id) {
    return managerService.findById(id);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteHotelManager(@PathVariable Integer id) {
    return managerService.deleteById(id);
  }

}
