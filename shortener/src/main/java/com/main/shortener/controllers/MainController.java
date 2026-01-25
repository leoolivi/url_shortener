package com.main.shortener.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.shortener.domain.models.UrlMapping;
import com.main.shortener.services.UrlMappingService;
import com.urlshortener.messaging.CreateMappingRequest;
import com.urlshortener.messaging.UpdateMappingRequest;

import lombok.AllArgsConstructor;




@RestController
@RequestMapping("/api/v1/shortener")
@AllArgsConstructor
public class MainController {
    
    private final UrlMappingService service;

    
    @GetMapping("mappings")
    public ResponseEntity<List<UrlMapping>> getMappings() {
        return ResponseEntity.ok(service.getMappings());
    }

    @PostMapping("mappings")
    public ResponseEntity<UrlMapping> createMapping(@RequestBody CreateMappingRequest request) {
        return ResponseEntity.ok(service.createMapping(request));
    }

    @DeleteMapping("mappings/{id}")
    public ResponseEntity<?> deleteMapping(@PathVariable Long id) {
        service.deleteMappingById(id);
        return ResponseEntity.ok("Mapping deleted successfully");
    }

    @PutMapping("mappings/{id}")
    public ResponseEntity<?> updateMapping(@PathVariable Long id, @RequestBody UpdateMappingRequest request) {
        service.updateMapping(request);
        return ResponseEntity.ok("Mapping updated successfully");
    }
    
    
}
