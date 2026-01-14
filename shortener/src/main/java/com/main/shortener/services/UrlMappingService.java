package com.main.shortener.services;

import java.util.List;
import org.springframework.stereotype.Service;

import com.main.shortener.domain.data.ShortenUrlRequest;
import com.main.shortener.domain.data.UpdateMappingRequest;
import com.main.shortener.domain.models.UrlMapping;
import com.main.shortener.exceptions.MappingNotFoundException;
import com.main.shortener.repositories.UrlMappingRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UrlMappingService {

    private final UrlMappingRepository repo;

    public List<UrlMapping> getMappings() {
        return repo.findAll();
    }

    public UrlMapping findMappingById(Long id) {
        return repo.findById(id).orElseThrow(() -> new MappingNotFoundException("Mapping not found"));
    }
    
    public UrlMapping createMapping(ShortenUrlRequest request) {
        var mapping = UrlMapping.builder()
                            .code(request.code())
                            .originalUrl(request.originalUrl())
                            .userId(request.userId())
                            .build();
        repo.save(mapping);
        return mapping;
    }

    public void deleteMapping(Long id) {
        repo.deleteById(id);
    }

    @Transactional
    public void updateMapping(UpdateMappingRequest request) {
        var mapping = repo.findById(request.getMappingId()).orElseThrow(() -> new MappingNotFoundException("Mapping not found"));
        if (mapping.getOriginalUrl() != request.getOriginalUrl()) mapping.setOriginalUrl(request.getOriginalUrl());
        if (mapping.getCode() != request.getCode()) mapping.setCode(request.getCode());
        repo.save(mapping);
    }

}