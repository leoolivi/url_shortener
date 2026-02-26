package com.main.redirector.services;

import org.springframework.stereotype.Service;

import com.main.redirector.domain.models.UrlMapping;
import com.main.redirector.exceptions.MappingNotFoundException;
import com.main.redirector.repositories.UrlMappingRepository;
import com.urlshortener.data.request.mapping.CreateMappingRequest;
import com.urlshortener.data.request.mapping.UpdateMappingRequest;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UrlMappingService {
    
    private final UrlMappingRepository repo;

    public UrlMapping createMapping(CreateMappingRequest request) {
        var mapping = new UrlMapping(request.code(), request.originalUrl());
        return repo.save(mapping);
    }

    public UrlMapping deleteMapping(String code) {
        var mapping = repo.findById(code).orElseThrow(() -> new MappingNotFoundException("Mapping not found"));
        repo.deleteById(code);
        return mapping;
    }

    @Transactional
    public UrlMapping updateMapping(UpdateMappingRequest request) {
        var mapping = repo.findById(request.code()).orElseThrow(() -> new MappingNotFoundException("Mapping not found"));
        if (request.code() != null) mapping.setCode(request.code());
        if (request.originalUrl() != null) mapping.setOriginalUrl(request.originalUrl());
        return repo.save(mapping);
    }

    public String getOriginalUrl(String code) {
        return repo.findById(code).orElseThrow(() -> new MappingNotFoundException("Mapping not found")).getOriginalUrl();
    }

}
