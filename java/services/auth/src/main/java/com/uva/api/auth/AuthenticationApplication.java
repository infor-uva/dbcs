package com.uva.api.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync // Enable asynchronous audit logging and background tasks
@EnableScheduling // Enable scheduled key rotation
@EnableCaching // Enable caching for JWKS endpoint
public class AuthenticationApplication {

  static void main(String[] args) {
    SpringApplication.run(AuthenticationApplication.class, args);
  }
}


