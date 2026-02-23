package com.main.shortener.services;

import java.util.List;

import org.springframework.stereotype.Component;

import com.main.shortener.domain.models.UrlMapping;
import com.urlshortener.data.request.mapping.CreateMappingRequest;
import com.urlshortener.data.request.mapping.MappingRequest;
import com.urlshortener.data.response.mapping.MappingResponse;

import lombok.AllArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Component
@AllArgsConstructor
public class CreateMappingRequestHandler implements MappingRequestHandler {

    private final UrlMappingService urlMappingService;
    private final ObjectMapper objectMapper;

    @Override
    public Class<?> getRequestClass() {
        return CreateMappingRequest.class;
    }

    @Override
    public List<MappingResponse> handleRequest(MappingRequest req) {
        CreateMappingRequest convertedRequest = objectMapper.convertValue(req, CreateMappingRequest.class);
        UrlMapping mapping = urlMappingService.createMapping(convertedRequest);
        return List.of(objectMapper.convertValue(mapping, MappingResponse.class));
    }
    
}
