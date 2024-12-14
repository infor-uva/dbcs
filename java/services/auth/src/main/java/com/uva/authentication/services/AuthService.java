package com.uva.authentication.services;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.uva.authentication.api.UserAPI;
import com.uva.authentication.models.LoginRequest;
import com.uva.authentication.models.RegisterRequest;
import com.uva.authentication.models.remote.User;
import com.uva.authentication.utils.JwtUtil;
import com.uva.authentication.utils.SecurityUtils;

@Service
public class AuthService {

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private UserAPI userAPI;

  private boolean authenticateUser(LoginRequest request, User user) {
    return (user != null)
        ? SecurityUtils.checkPassword(request.getPassword(), user.getPassword())
        : false;
  }

  /**
   * Log the user
   * 
   * @param loginRequest
   * @return token for identify the user
   * @throws HttpClientErrorException(FORBIDDEN) if the credentials are invalid
   */
  public String login(LoginRequest loginRequest) {
    User user = userAPI.getUserByEmail(loginRequest.getEmail());

    if (!authenticateUser(loginRequest, user)) {
      throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "Invalid credentials");
    }

    return jwtUtil.generateToken(user);
  }

  public String register(RegisterRequest registerRequest) {
    // Ciframos la contraseña
    String hashPass = SecurityUtils.encrypt(registerRequest.getPassword());
    registerRequest.setPassword(hashPass);
    // Registramos el usuario
    User user = userAPI.registerUser(registerRequest);
    LoginRequest logReq = new LoginRequest();
    BeanUtils.copyProperties(user, logReq);

    return login(logReq);
  }
}
