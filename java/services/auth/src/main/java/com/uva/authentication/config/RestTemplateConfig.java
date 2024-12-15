package com.uva.authentication.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.uva.authentication.interceptor.AuthHttpInterceptor;

@Configuration
public class RestTemplateConfig {

    @Autowired
    private AuthHttpInterceptor jwtInterceptor;

    @Bean
    RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(List.of(jwtInterceptor));

        return restTemplate;

    }
}
