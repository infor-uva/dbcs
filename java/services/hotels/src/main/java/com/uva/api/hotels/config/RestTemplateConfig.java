package com.uva.api.hotels.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean("simpleRestTemplate")
    RestTemplate simpleRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    RestTemplate restTemplate(RestTemplateInterceptor interceptor) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(List.of(interceptor));
        return restTemplate;
    }
}
