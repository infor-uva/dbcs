package com.uva.api.users.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.uva.api.users.models.Manager;
import com.uva.api.users.services.ManagerService;

@RestController
@RequestMapping("users/managers")
@CrossOrigin(origins = "*")
public class ManagerController {

  @Autowired
  private ManagerService managerService;

  @GetMapping
  public ResponseEntity<List<Manager>> getAllHotelManagers() {
    List<Manager> users = managerService.findAll();
    return ResponseEntity.ok(users);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Manager> getHotelManagerById(@PathVariable Integer id) {
    return ResponseEntity.ok(managerService.findById(id));
  }

  @DeleteMapping("/{id}")
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
