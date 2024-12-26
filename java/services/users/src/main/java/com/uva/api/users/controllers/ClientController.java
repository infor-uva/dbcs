package com.uva.api.users.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.uva.api.users.models.ClientStatus;
import com.uva.api.users.services.ClientService;

@RestController
@RequestMapping("users/clients")
@CrossOrigin(origins = "*")
public class ClientController {

  @Autowired
  private ClientService clientService;

  @GetMapping
  public ResponseEntity<?> getAllClients() {
    return clientService.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getClientById(@PathVariable int id) {
    return clientService.findById(id);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<?> updateClientState(@PathVariable int id, @RequestBody Map<String, String> json) {

    String strStatus = json.get("status");
    if (strStatus == null)
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing required fields");

    try {
      ClientStatus clientStatus = ClientStatus.valueOf(strStatus);
      return clientService.updateClientStatus(id, clientStatus);
    } catch (IllegalArgumentException e) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Unknown Client state");
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteClient(@PathVariable Integer id) {
    return clientService.deleteById(id);
  }
}
