package com.main.redirector.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.main.redirector.domain.models.UrlMapping;

public interface UrlMappingRepository extends JpaRepository<UrlMapping, String> {
}