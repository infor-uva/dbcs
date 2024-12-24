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

  // Clients
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

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteClient(@PathVariable Integer id) {
    try {
      return ResponseEntity.ok(clientService.deleteById(id));
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND)
        return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
      throw e;
    }
  }
}
