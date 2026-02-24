package com.main.shortener.services;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.main.shortener.domain.models.UrlMapping;
import com.main.shortener.exceptions.MappingAlreadyExistException;
import com.main.shortener.exceptions.MappingNotFoundException;
import com.main.shortener.repositories.UrlMappingRepository;
import com.urlshortener.data.request.mapping.CreateMappingRequest;
import com.urlshortener.data.request.mapping.UpdateMappingRequest;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UrlMappingService {

    private final UrlMappingRepository repo;
    private final MappingSyncPublisher syncPublisher;

    public List<UrlMapping> getMappings() {
        return repo.findAll();
    }

    public UrlMapping findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new MappingNotFoundException("Mapping not found"));
    }

    public UrlMapping findByUserId(Long id) {
        return repo.findByUserId(id).orElseThrow(() -> new MappingNotFoundException("Mapping not found"));
    }

    public UrlMapping findByCode(String code) {
        return repo.findByCode(code).orElseThrow(() -> new MappingNotFoundException("Mapping not found"));
    }
    
    public UrlMapping createMapping(CreateMappingRequest request) {
        var mapping = UrlMapping.builder()
                            .code(request.code())
                            .originalUrl(request.originalUrl())
                            .userId(request.userId())
                            .build();
        try {
            repo.save(mapping);
            syncPublisher.publishCreated(mapping);
        } catch (DataIntegrityViolationException e) {
            throw new MappingAlreadyExistException("Mapping already exists");
        }
        return mapping;
    }

    public UrlMapping deleteMappingById(Long id) {
        var mapping = repo.findById(id).orElseThrow(() -> new MappingNotFoundException("Mapping not found"));
        repo.deleteById(id);
        return mapping;
    }

    public UrlMapping deleteMappingByCode(String code) {
        var mapping = repo.findByCode(code).orElseThrow(() -> new MappingNotFoundException("Mapping not found"));
        repo.deleteByCode(code);
        return mapping;
    }

    @Transactional
    public UrlMapping updateMapping(UpdateMappingRequest request) {
        var mapping = repo.findById(request.id()).orElseThrow(() -> new MappingNotFoundException("Mapping not found"));
        if (!mapping.getOriginalUrl().equals(request.originalUrl())) mapping.setOriginalUrl(request.originalUrl());
        if (!mapping.getCode().equals(request.code())) mapping.setCode(request.code());
        repo.save(mapping);
        return mapping;
    }

}