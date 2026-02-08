package com.uva.api.auth.modules.auth;

import com.uva.api.auth.modules.auth.dto.LoginRequest;
import com.uva.api.auth.modules.auth.dto.RegisterRequest;
import com.uva.api.auth.modules.internal.UserAPI;
import com.uva.api.auth.modules.internal.dto.User;
import com.uva.api.auth.modules.jwt.JwtUtil;
import com.uva.api.auth.modules.jwt.dto.JwtAuthRequest;
import com.uva.api.auth.modules.jwt.dto.JwtDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.HttpClientErrorException;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final JwtUtil jwtUtil;
  private final UserAPI userAPI;

  private boolean authenticateUser(LoginRequest request, User user) {
    return user != null && SecurityUtils.checkPassword(request.password(), user.password());
  }

  /**
   * Log the user
   *
   * @param loginRequest
   * @return token for identify the user
   * @throws HttpClientErrorException(FORBIDDEN) if the credentials are invalid
   */
  public ResponseEntity<?> login(LoginRequest loginRequest) {
    User user = userAPI.getUserByEmail(loginRequest.email());

    if (!authenticateUser(loginRequest, user))
      throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "Invalid credentials");

    String token = jwtUtil.generateToken(user);
    return ResponseEntity.ok(new JwtAuthRequest(token));
  }

  public ResponseEntity<?> register(RegisterRequest registerRequest) {
    String plainTextPassword = registerRequest.password();
    // Ciframos la contraseña
    String hashPass = SecurityUtils.encrypt(plainTextPassword);
    RegisterRequest encryptedRequest = new RegisterRequest(
            registerRequest.email(), hashPass, registerRequest.rol(), registerRequest.name()
    );
    // Registramos el usuario
    User user = userAPI.registerUser(registerRequest);
    // Recuperamos la contraseña y lo loggeamos
    LoginRequest logReq = new LoginRequest(user.email(), hashPass);
    return login(logReq);
  }

  private User getUser(String email, String password) {
    return getUser(email, password, false);
  }

  private User getUser(String email, String password, boolean isAdmin) {
    User user = userAPI.getUserByEmail(email);
    boolean correctPassword = isAdmin || SecurityUtils.checkPassword(password, user.password());
    return correctPassword ? user : null;
  }

  public ResponseEntity<?> changePassword(
          String token, String email, @NonNull @Validated String actualPass, String newPass
  ) {
    JwtDataResponse decoded = jwtUtil.decodeToken(token);
    if (decoded == null)
      throw new HttpClientErrorException(HttpStatus.FORBIDDEN);

    User user = getUser(email, actualPass, decoded.isAdmin());

    boolean changePasswordAllowed = decoded.isAdmin() || (user != null);

    if (user != null)
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
    JwtDataResponse decoded = jwtUtil.decodeToken(token);
    if (decoded == null)
      throw new HttpClientErrorException(HttpStatus.FORBIDDEN);

    boolean deleteAllowed = decoded.isAdmin();
    if (!deleteAllowed) { // no admin
      String email = decoded.getEmail();

      User user = getUser(email, password);

      if (user == null)
        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);

      // Verificamos si es el dueño del recurso
      deleteAllowed = user.id() == id;
    }

    if (!deleteAllowed)
      throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "Invalid credentials");

    userAPI.deleteUser(id);
    return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }
}
