package com.uva.api.auth.config;

import com.uva.api.auth.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

  private final JwtUtil jwtUtil;

  @Override
  public @NotNull ClientHttpResponse intercept(
          @NotNull HttpRequest request,
          byte @NotNull [] body,
          @NotNull ClientHttpRequestExecution execution
  ) throws IOException {

    String token = jwtUtil.getOwnInternalToken();

    request.getHeaders().add("Authorization", "Bearer " + token);

    // Continuar con la solicitud
    return execution.execute(request, body);
  }
}
