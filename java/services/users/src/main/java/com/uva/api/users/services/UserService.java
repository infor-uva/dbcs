package com.uva.api.users.services;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.uva.api.users.models.AuthDTO;
import com.uva.api.users.models.Client;
import com.uva.api.users.models.Manager;
import com.uva.api.users.models.User;
import com.uva.api.users.models.UserRol;
import com.uva.api.users.repositories.UserRepository;
import com.uva.api.users.utils.Utils;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ClientService clientService;

  @Autowired
  private ManagerService managerService;

  @Autowired
  private TokenService tokenService;

  public ResponseEntity<List<User>> getAllUsers() {
    List<User> users = userRepository.findAll();
    return ResponseEntity.ok(users);
  }

  private User assertUserById(int id) {
    return Utils.assertUser(userRepository.findById(id));
  }

  public ResponseEntity<User> getUserById(String token, int id) {
    tokenService.assertPermission(token, id);
    User user = assertUserById(id);
    return ResponseEntity.ok(user);
  }

  public ResponseEntity<AuthDTO> getUserByEmail(String email) {
    User u = Utils.assertUser(userRepository.findByEmail(email));
    AuthDTO auth = new AuthDTO();
    BeanUtils.copyProperties(u, auth);
    return ResponseEntity.ok(auth);
  }

  public ResponseEntity<User> registerNewUser(AuthDTO request) {
    if (userRepository.existsByEmail(request.getEmail()))
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);

    User user = new User();
    BeanUtils.copyProperties(request, user);

    // Aseguramos que tenga un rol, por defecto es cliente
    if (user.getRol() == null)
      user.setRol(UserRol.CLIENT);

    switch (user.getRol()) {
      case ADMIN: // Not extracted due to its complexity, it's the same as for the user
        User admin = new User();
        BeanUtils.copyProperties(user, admin);
        user = userRepository.save(admin);
        break;

      case MANAGER:
        Manager manager = new Manager();
        BeanUtils.copyProperties(request, manager);
        user = managerService.save(manager).getBody();
        break;

      case CLIENT: // By default
      default:
        Client client = new Client();
        BeanUtils.copyProperties(request, client);
        user = clientService.save(client).getBody();
        break;
    }
    return ResponseEntity.ok(user);
  }

  public ResponseEntity<User> updateUserData(String token, int id, String name, String email) {
    tokenService.assertPermission(token, id);
    User user = assertUserById(id);
    user.setName(name);
    user.setEmail(email);
    user = userRepository.save(user);
    return ResponseEntity.ok(user);
  }

  public ResponseEntity<User> changePassword(String token, int id, String password) {
    tokenService.assertPermission(token, id);
    User user = assertUserById(id);
    user.setPassword(password);
    user = userRepository.save(user);
    return ResponseEntity.ok(user);
  }

  public ResponseEntity<User> deleteUserById(String token, int id) {
    tokenService.assertPermission(token, id);
    User user = assertUserById(id);
    switch (user.getRol()) {
      case CLIENT:
        clientService.deleteById(token, id);
        break;
      case MANAGER:
        managerService.deleteById(token, id);
        break;
      case ADMIN:
      default:
        userRepository.deleteById(id);
        break;
    }
    return ResponseEntity.ok(user);
  }
}
