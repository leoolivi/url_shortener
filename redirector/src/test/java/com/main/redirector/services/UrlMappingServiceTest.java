package com.main.redirector.services;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.main.redirector.domain.models.UrlMapping;
import com.main.redirector.exceptions.MappingNotFoundException;
import com.main.redirector.repositories.UrlMappingRepository;
import com.urlshortener.data.request.mapping.CreateMappingRequest;
import com.urlshortener.data.request.mapping.UpdateMappingRequest;

@ExtendWith(MockitoExtension.class)
public class UrlMappingServiceTest {

    @Mock
    private UrlMappingRepository repo;
    
    @InjectMocks
    private UrlMappingService service;
    
    @Test
    public void shouldAddMapping() {
        service.createMapping(new CreateMappingRequest("url", "test",  1L));
        verify(repo, times(1)).save(any(UrlMapping.class));

    }
    
    @Test 
    public void shouldGetMappingByCode() {
        UrlMapping expectedMapping = new UrlMapping("test", "url");
        when(repo.findById("test")).thenReturn(Optional.of(expectedMapping));

        String mapping = service.getOriginalUrl("test");
        assertEquals(expectedMapping.getOriginalUrl(), mapping);
    }
    
    @Test
    public void shouldUpdateMapping() {
        var originalMapping = new UrlMapping("test", "url_old");
        when(repo.findById("test")).thenReturn(Optional.of(originalMapping));

        service.updateMapping(new UpdateMappingRequest(1L, "url_updated", "test", 1L));
        assertEquals("url_updated", originalMapping.getOriginalUrl());
    }

    @Test
    public void shouldRemoveMapping() {
        var expectedMapping = new UrlMapping("test", "url");
        when(repo.findById("test")).thenReturn(Optional.of(expectedMapping));
        service.deleteMapping("test");
        verify(repo, times(1)).deleteById("test");
    }

    // Exception Testing

    @Test
    public void getMappingByCode_shouldThrowWhenMappingNotFound() {
        assertThrows(MappingNotFoundException.class, () -> service.getOriginalUrl("test-not-exists"));
    }



}
