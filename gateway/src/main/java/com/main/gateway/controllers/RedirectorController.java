package com.main.gateway.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.gateway.services.ProducerService;
import com.main.gateway.services.ResponseListener;
import com.urlshortener.data.MessageEnvelope;
import com.urlshortener.data.request.redirect.RedirectRequest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/v1/redirector")
@Slf4j
@AllArgsConstructor
public class RedirectorController {
    
    private final ProducerService service;
    private final ResponseListener listener;

    @GetMapping("/{code}")
    public ResponseEntity<?> redirect(@PathVariable String code) {
        MessageEnvelope<RedirectRequest> message = new MessageEnvelope<>();
        message.setCorrelationId(UUID.randomUUID().toString());
        message.setMessageType("MAPPING_REDIRECT");
        message.setPayload(new RedirectRequest(code));
        message.setSource("gateway");
        message.setTimestamp(System.currentTimeMillis());

        service.sendMessage("gateway.exchange", "redirect.get", message);
        MessageEnvelope<?> responseAsync = listener.waitComplete();
        log.info("Response Received: {}", responseAsync);
        return ResponseEntity.ok(responseAsync);
    }
    

}
