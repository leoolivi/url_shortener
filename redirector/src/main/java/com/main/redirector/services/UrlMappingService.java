package com.main.redirector.services;

import org.springframework.stereotype.Service;

import com.main.redirector.domain.data.CreateMappingRequest;
import com.main.redirector.domain.models.UrlMapping;
import com.main.redirector.exceptions.UrlMappingNotFoundException;
import com.main.redirector.repositories.UrlMappingRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UrlMappingService {
    
    private final UrlMappingRepository repo;

    public void createMapping(CreateMappingRequest request) {
        var mapping = new UrlMapping(request.code(), request.originalUrl());
        repo.save(mapping);
    }

    public String getOriginalUrl(String code) {
        return repo.findById(code).orElseThrow(() -> new UrlMappingNotFoundException("Mapping not found")).getOriginalUrl();
    }

}
