package com.uva.api.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Component;

import com.uva.api.models.external.users.UserRol;
import com.uva.api.utils.JwtUtil;

import java.io.IOException;

@Component
public class AuthHttpInterceptor implements ClientHttpRequestInterceptor {

  @Autowired
  private JwtUtil jwtUtil;

  private String token;

  private String getAccessToken() {
    if (token == null || token.isEmpty()) {
      // TODO cambiar también si el token ha caducado
      token = jwtUtil.generateToken("auth", "auth@dev.com", UserRol.ADMIN);
    }
    return token;

  }

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
      throws IOException {
    // Generar o cargar el JWT token desde el bean JwtUtil
    String jwtToken = getAccessToken();

    // System.out.println("Using token " + jwtToken);

    // Agregar el token al encabezado Authorization
    request.getHeaders().add("Authorization", "Bearer " + jwtToken);

    // Continuar con la ejecución de la solicitud
    return execution.execute(request, body);
  }
}
