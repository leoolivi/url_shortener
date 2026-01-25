package com.main.redirector.services;

import org.springframework.stereotype.Service;

import com.main.redirector.domain.data.CreateMappingRequest;
import com.main.redirector.domain.data.UpdateMappingRequest;
import com.main.redirector.domain.models.UrlMapping;
import com.main.redirector.exceptions.MappingNotFoundException;
import com.main.redirector.repositories.UrlMappingRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UrlMappingService {
    
    private final UrlMappingRepository repo;

    public void createMapping(CreateMappingRequest request) {
        var mapping = new UrlMapping(request.code(), request.originalUrl());
        repo.save(mapping);
    }

    public void deleteMapping(String code) {
        repo.deleteById(code);
    }

    @Transactional
    public void updateMapping(UpdateMappingRequest request) {
        var mapping = repo.findById(request.code()).orElseThrow(() -> new MappingNotFoundException("Mapping not found"));
        if (request.code() != null) mapping.setCode(request.code());
        if (request.originalUrl() != null) mapping.setOriginalUrl(request.originalUrl());
        repo.save(mapping);
    }

    public String getOriginalUrl(String code) {
        return repo.findById(code).orElseThrow(() -> new MappingNotFoundException("Mapping not found")).getOriginalUrl();
    }

}
