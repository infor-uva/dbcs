package com.uva.api.auth.services;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.uva.api.auth.api.UserAPI;
import com.uva.api.auth.models.auth.LoginRequest;
import com.uva.api.auth.models.auth.RegisterRequest;
import com.uva.api.auth.models.jwt.JwtAuth;
import com.uva.api.auth.models.jwt.JwtData;
import com.uva.api.auth.models.remote.User;
import com.uva.api.auth.utils.JwtUtil;
import com.uva.api.auth.utils.SecurityUtils;

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
    return getUser(email, password, false);
  }

  private User getUser(String email, String password, boolean isAdmin) {
    User user = userAPI.getUserByEmail(email);
    boolean correctPassword = isAdmin || SecurityUtils.checkPassword(password, user.getPassword());
    return correctPassword ? user : null;
  }

  public ResponseEntity<?> changePassword(String token, String email, String actualPass, String newPass) {
    JwtData decoded = jwtUtil.decodeToken(token);
    if (decoded == null)
      throw new HttpClientErrorException(HttpStatus.FORBIDDEN);

    User user = getUser(email, actualPass, decoded.isAdmin());

    boolean changePasswordAllowed = decoded.isAdmin() || (user != null && validStrings(actualPass));

    if (user != null && !validStrings(newPass))
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);

    if (!changePasswordAllowed)
      throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "Invalid credentials");

    // Actualizamos la nueva
    String hashPass = SecurityUtils.encrypt(newPass);
    userAPI.changePassword(user, hashPass);
    // Hacemos un login con los nuevos datos
    return login(new LoginRequest(email, newPass));
  }

  public ResponseEntity<?> deleteUser(String token, int id, String password) {
    JwtData decoded = jwtUtil.decodeToken(token);
    if (decoded == null)
      throw new HttpClientErrorException(HttpStatus.FORBIDDEN);

    boolean deleteAllowed = decoded.isAdmin();
    if (!deleteAllowed) { // no admin
      String email = decoded.getEmail();

      User user = getUser(email, password);

      if (user == null || !validStrings(password))
        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);

      // Verificamos si es el dueño del recurso
      deleteAllowed = user.getId() == id;
    }

    if (!deleteAllowed)
      throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "Invalid credentials");

    userAPI.deleteUser(id);
    return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }
}
