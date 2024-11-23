package com.uva.authentication.services;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.uva.authentication.api.UserAPI;
import com.uva.authentication.jwt.JwtUtil;
import com.uva.authentication.models.LoginRequest;
import com.uva.authentication.models.RegisterRequest;
import com.uva.authentication.models.User;

@Service
public class AuthService {

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private UserAPI userAPI;

  private String hashPass(String password) {
    return String.valueOf(Objects.hashCode(password));
  }

  private boolean authenticateUser(LoginRequest request, User user) {
    System.err.println(user);
    if (user == null)
      return false;
    String hashPass = hashPass(request.getPassword());
    System.err.println(request.getPassword() + " -> " + hashPass + " == " +
        user.getPassword());
    return hashPass.equals(user.getPassword());
  }

  public String login(LoginRequest loginRequest) {
    User user = userAPI.getUserByEmail(loginRequest.getEmail());
    boolean isAuthenticated = authenticateUser(loginRequest, user);

    if (!isAuthenticated) {
      throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "Invalid credentials");
    }

    // Generate a mock JWT token for simplicity
    String jwtToken = "Bearer " + jwtUtil.generateToken(user);
    return jwtToken;

  }

  public User register(RegisterRequest registerRequest) {
    User user = userAPI.getUserByEmail(registerRequest.getEmail());
    if (user != null)
      throw new HttpClientErrorException(HttpStatus.CONFLICT, "Email already in use");

    String hashPass = hashPass(registerRequest.getPassword());
    registerRequest.setPassword(hashPass);
    return userAPI.registerUser(registerRequest);
  }

}
