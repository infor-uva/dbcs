package com.uva.authentication.services;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.uva.authentication.api.UserAPI;
import com.uva.authentication.models.auth.LoginRequest;
import com.uva.authentication.models.auth.RegisterRequest;
import com.uva.authentication.models.jwt.JwtAuth;
import com.uva.authentication.models.jwt.JwtData;
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
  public ResponseEntity<?> login(LoginRequest loginRequest) {
    User user = userAPI.getUserByEmail(loginRequest.getEmail());

    if (!authenticateUser(loginRequest, user))
      throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "Invalid credentials");

    String token = jwtUtil.generateToken(user);
    return ResponseEntity.ok(new JwtAuth(token));
  }

  public ResponseEntity<?> register(RegisterRequest registerRequest) {
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
    return login(logReq);
  }

  private boolean validStrings(String... args) {
    for (String arg : args) {
      if (arg == null || arg.isBlank())
        return false;
    }
    return true;
  }

  private User getUser(String email, String password) {
    User user = userAPI.getUserByEmail(email);
    boolean correctPassword = SecurityUtils.checkPassword(password, user.getPassword());
    return correctPassword ? user : null;
  }

  public ResponseEntity<?> changePassword(String token, String actualPass, String newPass) {
    JwtData decoded = jwtUtil.decodeToken(token);
    if (decoded == null)
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);

    String email = decoded.getEmail();
    User user = getUser(email, actualPass);

    boolean changePasswordAllowed = decoded.isAdmin() || user != null;

    if (user != null && !validStrings(actualPass, newPass))
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    if (changePasswordAllowed) {
      // Actualizamos la nueva
      String hashPass = SecurityUtils.encrypt(newPass);
      userAPI.changePassword(user, hashPass);
      // Hacemos un login con los nuevos datos
      return login(new LoginRequest(email, newPass));
    } else {
      return new ResponseEntity<>("Invalid credentials", HttpStatus.FORBIDDEN);
    }
  }

  public ResponseEntity<?> deleteUser(String token, int id, String password) {
    JwtData decoded = jwtUtil.decodeToken(token);
    if (decoded == null)
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);

    String email = decoded.getEmail();
    User user = getUser(email, password);

    boolean changePasswordAllowed = decoded.isAdmin()
        || (user != null && user.getId() == id);

    if (user != null && !validStrings(password))
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    if (changePasswordAllowed) {
      userAPI.deleteUser(user);
      return new ResponseEntity<>(HttpStatus.ACCEPTED);
    } else {
      return new ResponseEntity<>("Invalid credentials", HttpStatus.FORBIDDEN);
    }
  }
}
