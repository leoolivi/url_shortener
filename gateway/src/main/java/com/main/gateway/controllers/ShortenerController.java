package com.main.gateway.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.main.gateway.domain.data.ShortenUrlRequest;
import com.main.gateway.services.ProducerService;
import com.main.gateway.services.ResponseListener;
import com.urlshortener.messaging.CreateMappingRequest;
import com.urlshortener.messaging.MessageEnvelope;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/v1/shortener")
@AllArgsConstructor
@Slf4j
public class ShortenerController {

    private final ProducerService service;
    private final ResponseListener listener;

    @PostMapping("mapping")
    public ResponseEntity<?> createMapping(@RequestBody CreateMappingRequest request) {
        MessageEnvelope<CreateMappingRequest> message = new MessageEnvelope<>();
        message.setCorrelationId(UUID.randomUUID().toString());
        message.setMessageType("MAPPING_CREATED");
        message.setSource("gateway");
        message.setTimestamp(System.currentTimeMillis());
        message.setPayload(request);
        service.sendMessage("main.exchange", "mapping.created", message);
        MessageEnvelope<?> responseAsync = listener.waitComplete();
        log.info("Response Received: {}", responseAsync);
        return ResponseEntity.ok(responseAsync);
    }
    
}
