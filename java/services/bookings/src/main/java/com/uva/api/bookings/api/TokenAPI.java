package com.uva.api.bookings.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.uva.api.bookings.models.external.jwt.JwtData;

@Component
public class TokenAPI {

  private final RestTemplate restTemplate;

  public TokenAPI(@Qualifier("simpleRestTemplate") RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Value("${spring.application.name}")
  private String service;

  @Value("${services.internal.token.url}")
  private String TOKEN_API_URL;

  public JwtData getServiceToken() {
    String url = TOKEN_API_URL + "/service";
    Map<String, String> body = new HashMap<>();
    body.put("service", service);
    String token = restTemplate.postForObject(url, body, JsonNode.class)
        .get("token").asText();
    return decodeToken(token);
  }

  public JwtData decodeToken(String token) {
    String url = TOKEN_API_URL + "/info";
    Map<String, String> body = new HashMap<>();
    body.put("token", token);
    JwtData response = restTemplate.postForObject(url, body, JwtData.class);
    response.setToken(token);
    return response;
  }

}
