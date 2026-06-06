package com.thehalfspace.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class FootballApiConfig {

    @Value("${football.api.base-url}")
    private String baseUrl;

    @Value("${football.api.token}")
    private String token;

    @Bean
    public RestClient footballRestClient() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-Auth-Token", token)
                .build();
    }
}
