package com.main.gateway.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.gateway.services.ProducerService;
import com.main.gateway.services.ResponseListener;
import com.urlshortener.messaging.AuthenticateRequest;
import com.urlshortener.messaging.MessageEnvelope;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
@Slf4j
public class AuthController {
    private final ProducerService producer;
    private final ResponseListener listener;

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody AuthenticateRequest request) {
        MessageEnvelope<AuthenticateRequest> message = new MessageEnvelope<>();
        message.setCorrelationId(UUID.randomUUID().toString());
        message.setMessageType("AUTH_LOGIN");
        message.setPayload(request);
        message.setSource("gateway");
        message.setTimestamp(System.currentTimeMillis());
    
        producer.sendMessage("gateway.exchange", "auth.login", message);
        var response = listener.waitComplete();
        log.info("Received response: {}", response);
        return ResponseEntity.ok(response);
    }
    
}
