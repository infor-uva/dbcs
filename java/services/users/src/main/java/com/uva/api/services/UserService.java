package com.uva.api.services;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uva.api.utils.Utils;
import com.uva.api.models.AuthResponse;
import com.uva.api.models.User;
import com.uva.api.models.UserRol;
import com.uva.api.repositories.UserRepository;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ClientService clientService;

  @Autowired
  private ManagerService managerService;

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  public User getUserById(int id) {
    return Utils.assertUser(userRepository.findById(id));
  }

  public AuthResponse getUserByEmail(String email) {
    User u = Utils.assertUser(userRepository.findByEmail(email));
    AuthResponse auth = new AuthResponse();
    BeanUtils.copyProperties(u, auth);
    return auth;
  }

  public User registerNewUser(User registerRequest) {
    User newUser;

    // Aseguramos que tenga un rol, por defecto es cliente
    if (registerRequest.getRol() == null)
      registerRequest.setRol(UserRol.CLIENT);

    switch (registerRequest.getRol()) {
      case ADMIN: // Not extracted due to its complexity, it's the same as for the user
        User admin = new User();
        BeanUtils.copyProperties(registerRequest, admin);
        newUser = userRepository.save(admin);
        break;

      case HOTEL_ADMIN:
        newUser = managerService.save(registerRequest);
        break;

      case CLIENT: // By default
      default:
        newUser = clientService.save(registerRequest);
        break;
    }
    return newUser;
  }

  public User updateUserData(int id, String name, String email) {
    User user = getUserById(id);
    user.setName(name);
    user.setEmail(email);
    return userRepository.save(user);
  }

  public User changePassword(int id, String password) {
    User user = getUserById(id);
    user.setPassword(password);
    return userRepository.save(user);
  }

  public User deleteUserById(int id) {
    User user = getUserById(id);
    userRepository.deleteById(id);
    return user;
  }
}
