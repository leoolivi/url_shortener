package com.main.shortener.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.urlshortener.security.JwtVerifier;

@Configuration
public class ApplicationConfiguration {
    @Bean
    public JwtVerifier jwtVerifier() {
        return new JwtVerifier();
    }
}
