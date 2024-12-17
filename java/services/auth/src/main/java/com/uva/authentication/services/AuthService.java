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
    String plainTextPassword = registerRequest.getPassword();
    // Ciframos la contraseña
    String hashPass = SecurityUtils.encrypt(plainTextPassword);
    registerRequest.setPassword(hashPass);
    // Registramos el usuario
    User user = userAPI.registerUser(registerRequest);
    LoginRequest logReq = new LoginRequest();
    BeanUtils.copyProperties(user, logReq);
    // Recuperamos la contraseña y lo loggeamos
    logReq.setPassword(plainTextPassword);
    System.err.println(logReq);
    return login(logReq);
  }

  public String changePassword(String email, String actualPass, String newPass) {
    User user = userAPI.getUserByEmail(email);
    // Validamos la anterior contraseña
    if (SecurityUtils.checkPassword(actualPass, user.getPassword())) {
      // Actualizamos la nueva
      String hashPass = SecurityUtils.encrypt(newPass);
      userAPI.changePassword(user, hashPass);
      // Hacemos un login con los nuevos datos
      return login(new LoginRequest(email, newPass));
    } else {
      throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "Invalid credentials");
    }
  }
}
