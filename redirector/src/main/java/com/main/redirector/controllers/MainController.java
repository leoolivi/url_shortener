package com.main.redirector.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.redirector.services.UrlMappingService;

import lombok.AllArgsConstructor;


@RestController
@RequestMapping("api/v1/redirector")
@AllArgsConstructor
public class MainController {

    private final UrlMappingService service;

    @GetMapping("/{code}")
    public ResponseEntity<?> redirect(@PathVariable String code) {
        return ResponseEntity.ok(service.getOriginalUrl(code));
    }
    
}
