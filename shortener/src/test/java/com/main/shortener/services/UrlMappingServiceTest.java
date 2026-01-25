package com.main.shortener.services;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.main.shortener.domain.models.UrlMapping;
import com.main.shortener.repositories.UrlMappingRepository;

@ExtendWith(MockitoExtension.class)
public class UrlMappingServiceTest {
    @Mock
    private UrlMappingRepository urlMappingRepository;

    @InjectMocks
    private UrlMappingService urlMappingService;

    @BeforeEach  // Cambiato da @BeforeAll a @BeforeEach
    public void setup() {
        // Setup dei mock necessari
    }

    @Test
    public void shouldAddMapping() {
        var mockMapping = Optional.of(new UrlMapping(1L, "a", "o", 1L));
        when(urlMappingRepository.findByUserId(1L)).thenReturn(mockMapping);
        
        var result = urlMappingService.findByUserId(1L);
        
        assertEquals(mockMapping.get(), result);
    }

    @Test 
    public void shouldGetMappingByCode() {
        // Mock del comportamento del repository
        var expectedMapping = new UrlMapping(1L, "yh", "www.yahoo.com", 1L);
        when(urlMappingRepository.findByCode("yh"))
            .thenReturn(Optional.of(expectedMapping));
        
        var foundMapping = urlMappingService.findByCode("yh");
        
        assertEquals(expectedMapping, foundMapping);
    }
}