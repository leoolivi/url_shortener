package com.main.shortener.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.shortener.domain.data.ShortenUrlRequest;
import com.main.shortener.domain.models.UrlMapping;
import com.main.shortener.services.UrlMappingService;

import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1/shortener")
@AllArgsConstructor
public class MainController {
    
    private final UrlMappingService service;

    @GetMapping("mappings")
    public List<UrlMapping> getMappings() {
        return service.getMappings();
    }

    @PostMapping("mappings")
    public UrlMapping createMapping(@RequestBody ShortenUrlRequest request) {
        return service.createMapping(request);
    }
    
    
}
