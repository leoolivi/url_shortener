package com.urlshortener.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Configuration
@ConfigurationProperties(prefix="security.jwt")
public class JwtProperties {
    private TokenConfig user;
    private TokenConfig internal;    
}
