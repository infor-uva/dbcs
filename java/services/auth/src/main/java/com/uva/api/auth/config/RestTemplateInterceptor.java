package com.uva.api.auth.config;

import org.springframework.http.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import com.uva.api.auth.utils.JwtUtil;

import java.io.IOException;

@Component
public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

  @Autowired
  private JwtUtil jwtUtil;

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
      throws IOException {

    // Añadir el encabezado "Authorization" con el valor "Bearer <token>"
    HttpHeaders headers = request.getHeaders();
    headers.add("Authorization",
        "Bearer " + jwtUtil.getOwnInternalToken());

    // Continuar con la solicitud
    return execution.execute(request, body);
  }
}
