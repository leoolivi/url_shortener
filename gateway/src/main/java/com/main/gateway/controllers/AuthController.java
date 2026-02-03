package com.main.gateway.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.urlshortener.data.AuthenticateRequest;
import com.urlshortener.data.AuthenticateResponse;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
@Slf4j
public class AuthController {

    private final WebClient webClient;
    
    @PostMapping("login")
    public Mono<ResponseEntity<?>> login(@RequestBody AuthenticateRequest request) {
        log.info(
            "sending request {}", request
        );        
        return webClient.post()
            .uri("/api/v1/auth/login")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(AuthenticateResponse.class)
            .map(response -> ResponseEntity.ok(response));
    }
    
}
