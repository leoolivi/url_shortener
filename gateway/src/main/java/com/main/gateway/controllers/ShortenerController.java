package com.main.gateway.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.gateway.services.ProducerService;
import com.main.gateway.services.ResponseListener;
import com.urlshortener.data.MessageEnvelope;
import com.urlshortener.data.request.mapping.CreateMappingRequest;
import com.urlshortener.data.request.mapping.DeleteMappingRequest;
import com.urlshortener.data.request.mapping.GetAllMappingsRequest;
import com.urlshortener.data.request.mapping.GetMappingRequest;
import com.urlshortener.data.request.mapping.UpdateMappingRequest;
import com.urlshortener.security.JwtAuthenticationToken;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;




@RestController
@RequestMapping("/api/v1/shortener")
@AllArgsConstructor
@Slf4j
public class ShortenerController {

    private final ProducerService service;
    private final ResponseListener listener;

    @GetMapping("mapping/{code}")
    public ResponseEntity<?> getMapping(@PathVariable String code) {
        MessageEnvelope<GetMappingRequest> message = new MessageEnvelope<>();
        message.setCorrelationId(UUID.randomUUID().toString());
        message.setMessageType("MAPPING_FIND");
        message.setSource("gateway");
        message.setTimestamp(System.currentTimeMillis());
        JwtAuthenticationToken auth = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        message.setToken(auth.getToken());
        message.setPayload(new GetMappingRequest(code));
        service.sendMessage("gateway.exchange", "mapping.find", message);
        MessageEnvelope<?> responseAsync = listener.waitComplete();
        log.info("Response Received: {}", responseAsync);
        return ResponseEntity.ok(responseAsync);
    }

    @GetMapping("mapping")
    public ResponseEntity<?> getAllMappings() {
        MessageEnvelope<GetAllMappingsRequest> message = new MessageEnvelope<>();
        message.setCorrelationId(UUID.randomUUID().toString());
        message.setMessageType("MAPPING_FINDALL");
        message.setSource("gateway");
        message.setTimestamp(System.currentTimeMillis());
        JwtAuthenticationToken auth = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        message.setToken(auth.getToken());
        message.setPayload(new GetAllMappingsRequest());
        service.sendMessage("gateway.exchange", "mapping.findall", message);
        MessageEnvelope<?> responseAsync = listener.waitComplete();
        log.info("Response Received: {}", responseAsync);
        return ResponseEntity.ok(responseAsync);
    }
    
    
    
    @PostMapping("mapping")
    public ResponseEntity<?> createMapping(@RequestBody CreateMappingRequest request) {
        MessageEnvelope<CreateMappingRequest> message = new MessageEnvelope<>();
        message.setCorrelationId(UUID.randomUUID().toString());
        message.setMessageType("MAPPING_CREATED");
        message.setSource("gateway");
        message.setTimestamp(System.currentTimeMillis());
        message.setPayload(request);
        JwtAuthenticationToken auth = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        message.setToken(auth.getToken());
        service.sendMessage("gateway.exchange", "mapping.created", message);
        MessageEnvelope<?> responseAsync = listener.waitComplete();
        log.info("Response Received: {}", responseAsync);
        return ResponseEntity.ok(responseAsync);
    }

    @DeleteMapping("mapping/{code}")
    public ResponseEntity<?> deleteMapping(@PathVariable String code ) {
        MessageEnvelope<DeleteMappingRequest> message = new MessageEnvelope<>();
        message.setCorrelationId(UUID.randomUUID().toString());
        message.setMessageType("MAPPING_DELETED");
        message.setSource("gateway");
        message.setTimestamp(System.currentTimeMillis());
        JwtAuthenticationToken auth = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        message.setToken(auth.getToken());
        message.setPayload(new DeleteMappingRequest(code));
        service.sendMessage("gateway.exchange", "mapping.deleted", message);
        service.sendMessage("data.sync.exchange", "sync.mapping.deleted", message);
        MessageEnvelope<?> responseAsync = listener.waitComplete();
        log.info("Response Received: {}", responseAsync);
        return ResponseEntity.ok(responseAsync);
    }

    @PutMapping("mapping")
    public ResponseEntity<?> updateMapping(@RequestBody UpdateMappingRequest request ) {
        MessageEnvelope<UpdateMappingRequest> message = new MessageEnvelope<>();
        message.setCorrelationId(UUID.randomUUID().toString());
        message.setMessageType("MAPPING_UPDATED");
        message.setSource("gateway");
        message.setTimestamp(System.currentTimeMillis());
        JwtAuthenticationToken auth = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        message.setToken(auth.getToken());
        message.setPayload(request);
        service.sendMessage("gateway.exchange", "mapping.updated", message);
        service.sendMessage("data.sync.exchange", "sync.mapping.updated", message);
        MessageEnvelope<?> responseAsync = listener.waitComplete();
        log.info("Response Received: {}", responseAsync);
        return ResponseEntity.ok(responseAsync);
    }
    
}
