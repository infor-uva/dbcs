package com.uva.api.hotels.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Component;

import com.uva.api.hotels.services.TokenService;

import java.io.IOException;

@Component
public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

  @Autowired
  private TokenService service;

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
      throws IOException {

    String jwtToken = service.getServiceToken();

    request.getHeaders().add("Authorization", "Bearer " + jwtToken);

    return execution.execute(request, body);
  }
}
