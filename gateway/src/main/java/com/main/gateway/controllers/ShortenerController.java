package com.main.gateway.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.gateway.domain.data.ShortenUrlRequest;
import com.main.gateway.services.ProducerService;

import lombok.AllArgsConstructor;


@RestController
@RequestMapping("/api/v1/shortener")
@AllArgsConstructor
public class ShortenerController {

    private final ProducerService service;

    @PostMapping("mapping")
    public ResponseEntity<String> createMapping(@RequestBody ShortenUrlRequest request) {
        service.sendMessage("main.exchange", "mapping.created", request);
        return ResponseEntity.ok("Mapping created");
    }
    
}
