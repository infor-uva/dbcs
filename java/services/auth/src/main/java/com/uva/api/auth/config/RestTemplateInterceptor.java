package com.uva.api.auth.config;

import org.springframework.http.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
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

    String token = jwtUtil.getOwnInternalToken();

    request.getHeaders().add("Authorization", "Bearer " + token);

    // Continuar con la solicitud
    return execution.execute(request, body);
  }
}
