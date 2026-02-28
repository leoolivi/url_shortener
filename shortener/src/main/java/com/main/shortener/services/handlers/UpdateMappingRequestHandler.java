package com.main.shortener.services.handlers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.main.shortener.domain.models.UrlMapping;
import com.main.shortener.services.UrlMappingService;
import com.urlshortener.data.request.mapping.MappingRequest;
import com.urlshortener.data.request.mapping.UpdateMappingRequest;
import com.urlshortener.data.response.mapping.MappingResponse;

import lombok.AllArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Component
@AllArgsConstructor
public class UpdateMappingRequestHandler implements MappingRequestHandler {

    private final UrlMappingService urlMappingService;
    private final ObjectMapper objectMapper;

    @Override
    public Class<?> getRequestClass() {
        return UpdateMappingRequest.class;
    }

    @Override
    public List<MappingResponse> handleRequest(MappingRequest req) {
        UpdateMappingRequest convertedRequest = objectMapper.convertValue(req, UpdateMappingRequest.class);
        UrlMapping mapping = urlMappingService.updateMapping(convertedRequest);
        return List.of(objectMapper.convertValue(mapping, MappingResponse.class));
    }
    
}
