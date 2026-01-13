package com.main.shortener.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.main.shortener.domain.models.UrlMapping;


public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {
    public Optional<UrlMapping> findByShortMapping(String shortMapping);
}
