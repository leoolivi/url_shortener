package com.main.gateway.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urlshortener.security.JwtProperties;
import com.urlshortener.security.jwt.verifier.InternalJwtVerifier;
import com.urlshortener.security.jwt.verifier.UserJwtVerifier;
import com.urlshortener.security.keys.internal.InternalEnvBase64KeyProvider;
import com.urlshortener.security.keys.internal.InternalKeyProvider;
import com.urlshortener.security.keys.internal.InternalTokenPolicyProvider;
import com.urlshortener.security.keys.user.UserEnvBase64KeyProvider;
import com.urlshortener.security.keys.user.UserKeyProvider;
import com.urlshortener.utils.KeyPairGeneratorUtil;
import com.urlshortener.utils.UserMapper;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public WebClient webClient() {
        return WebClient.create("http://localhost:3000");
    }

    @Bean
    public UserJwtVerifier userJwtVerifier(UserKeyProvider keyProvider, ObjectMapper objectMapper) {
        return new UserJwtVerifier(keyProvider, objectMapper); 
    }

    @Bean
    public InternalJwtVerifier internalJwtVerifier(InternalKeyProvider keyProvider, InternalTokenPolicyProvider tokenPolicyProvider) {
        return new InternalJwtVerifier(keyProvider, tokenPolicyProvider); 
    }

    @Bean
    public InternalTokenPolicyProvider internalTokenPolicyProvider(JwtProperties jwtProperties) {
        return new InternalTokenPolicyProvider(jwtProperties);
    }

    @Bean
    public UserKeyProvider userKeyProvider(JwtProperties jwtProperties) {
        return new UserEnvBase64KeyProvider(jwtProperties);
    }


    @Bean
    public InternalKeyProvider internalKeyProvider(JwtProperties jwtProperties) {
        return new InternalEnvBase64KeyProvider(jwtProperties);
    }

    @Bean 
    public JwtProperties jwtProperties() {
        return new JwtProperties();
    }

    @Bean
    public UserMapper userMapper() {
        return new UserMapper();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public KeyPairGeneratorUtil keyPairGeneratorUtil() {
        return new KeyPairGeneratorUtil();
    }
}
