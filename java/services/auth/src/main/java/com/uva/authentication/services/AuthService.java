package com.uva.authentication.services;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.uva.authentication.models.LoginRequest;
import com.uva.authentication.models.RegisterRequest;
import com.uva.authentication.models.remote.Client;
import com.uva.authentication.models.remote.HotelManager;
import com.uva.authentication.models.remote.User;
import com.uva.authentication.models.remote.UserRol;
import com.uva.authentication.repositories.ClientRepository;
import com.uva.authentication.repositories.HotelManagerRepository;
import com.uva.authentication.repositories.UserRepository;
import com.uva.authentication.utils.JwtUtil;
import com.uva.authentication.utils.SecurityUtils;

@Service
public class AuthService {

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private HotelManagerRepository hotelManagerRepository;

  @Autowired
  private ClientRepository clientRepository;

  @Autowired
  private UserRepository userRepository;

  private boolean authenticateUser(LoginRequest request, User user) {
    if (user == null)
      return false;
    return SecurityUtils.checkPassword(request.getPassword(), user.getPassword());
  }

  public String login(LoginRequest loginRequest) {
    User user = userRepository.findByEmail(loginRequest.getEmail())
        .orElseThrow(() -> new HttpClientErrorException(HttpStatus.FORBIDDEN,
            "Invalid credentials"));

    if (!authenticateUser(loginRequest, user)) {
      throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "Invalid credentials");
    }

    return jwtUtil.generateToken(user);
  }

  public User register(RegisterRequest registerRequest) {
    Optional<User> user = userRepository.findByEmail(registerRequest.getEmail());
    if (user.isPresent())
      throw new HttpClientErrorException(HttpStatus.CONFLICT, "Email already in use");

    return registerNewUser(registerRequest);
  }

  private User registerNewUser(RegisterRequest registerRequest) {
    User newUser;

    // Ciframos la contraseña
    String hashPass = SecurityUtils.encrypt(registerRequest.getPassword());
    registerRequest.setPassword(hashPass);

    // Aseguramos que tenga un rol, por defecto es cliente
    if (registerRequest.getRol() == null)
      registerRequest.setRol(UserRol.CLIENT);

    switch (registerRequest.getRol()) {
      case HOTEL_ADMIN:
        HotelManager hm = new HotelManager();
        BeanUtils.copyProperties(registerRequest, hm);
        newUser = hotelManagerRepository.save(hm);
        break;

      case ADMIN: // TODO revisar
        User admin = new User();
        BeanUtils.copyProperties(registerRequest, admin);
        newUser = userRepository.save(admin);
        break;

      case CLIENT: // Por defecto cliente normal
      default:
        Client client = new Client();
        BeanUtils.copyProperties(registerRequest, client);
        client.setRol(UserRol.CLIENT);
        newUser = clientRepository.save(client);
        break;
    }
    return newUser;
  }
}
