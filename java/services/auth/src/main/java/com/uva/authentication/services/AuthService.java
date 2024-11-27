package com.uva.authentication.services;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.uva.authentication.api.UserAPI;
import com.uva.authentication.models.Client;
import com.uva.authentication.models.HotelManager;
import com.uva.authentication.models.LoginRequest;
import com.uva.authentication.models.RegisterRequest;
import com.uva.authentication.models.User;
import com.uva.authentication.models.UserRol;
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
  private UserAPI userAPI;

  @Autowired
  private UserRepository userRepository;

  private boolean authenticateUser(LoginRequest request, User user) {
    if (user == null)
      return false;
    return SecurityUtils.checkPassword(request.getPassword(), user.getPassword());
  }

  public String login(LoginRequest loginRequest) {
    // User user = userAPI.getUserByEmail(loginRequest.getEmail());
    User user = userRepository.findByEmail(loginRequest.getEmail())
        .orElseThrow(() -> new HttpClientErrorException(HttpStatus.FORBIDDEN,
            "Invalid credentials"));
    boolean isAuthenticated = authenticateUser(loginRequest, user);

    if (!isAuthenticated) {
      throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "Invalid credentials");
    }

    // Generate a mock JWT token for simplicity
    String jwtToken = "Bearer " + jwtUtil.generateToken(user);
    return jwtToken;

  }

  public User register(RegisterRequest registerRequest) {
    // User user = userAPI.getUserByEmail(registerRequest.getEmail());
    Optional<User> user = userRepository.findByEmail(registerRequest.getEmail());
    if (user.isPresent())
      throw new HttpClientErrorException(HttpStatus.CONFLICT, "Email already in use");

    String hashPass = SecurityUtils.encrypt(registerRequest.getPassword());
    // return userAPI.registerUser(registerRequest);
    User newUser;
    if (registerRequest.getRol() == UserRol.HOTEL_ADMIN) {
      HotelManager hm = new HotelManager();
      // hm.setName(registerRequest.getName());
      // hm.setEmail(registerRequest.getEmail());
      // hm.setRol(registerRequest.getRol());
      BeanUtils.copyProperties(registerRequest, hm);
      hm.setPassword(hashPass);
      newUser = hotelManagerRepository.save(hm);
    } else {
      Client client = new Client();
      // client.setName(registerRequest.getName());
      // client.setEmail(registerRequest.getEmail());
      BeanUtils.copyProperties(registerRequest, client);
      client.setRol(UserRol.CLIENT);
      client.setPassword(hashPass);
      newUser = clientRepository.save(client);
    }
    return newUser;
  }

}
