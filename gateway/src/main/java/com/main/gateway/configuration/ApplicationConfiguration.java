package com.main.gateway.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import com.urlshortener.security.JwtVerifier;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public WebClient webClient() {
        return WebClient.create("http://localhost:3000");
    }

    @Bean
    public JwtVerifier jwtVerifier() {
        return new JwtVerifier(); 
    }
}
