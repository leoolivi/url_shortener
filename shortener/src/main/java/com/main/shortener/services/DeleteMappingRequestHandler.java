package com.main.shortener.services;

import java.util.List;

import org.springframework.stereotype.Component;

import com.main.shortener.domain.models.UrlMapping;
import com.urlshortener.data.request.mapping.DeleteMappingRequest;
import com.urlshortener.data.request.mapping.MappingRequest;
import com.urlshortener.data.response.mapping.MappingResponse;

import lombok.AllArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Component
@AllArgsConstructor
public class DeleteMappingRequestHandler implements MappingRequestHandler {

    private final UrlMappingService urlMappingService;
    private final ObjectMapper objectMapper;

    @Override
    public Class<?> getRequestClass() {
        return DeleteMappingRequest.class;
    }

    @Override
    public List<MappingResponse> handleRequest(MappingRequest req) {
        DeleteMappingRequest convertedRequest = objectMapper.convertValue(req, DeleteMappingRequest.class);
        UrlMapping mapping = urlMappingService.deleteMappingByCode(convertedRequest.code());
        return List.of(objectMapper.convertValue(mapping, MappingResponse.class));
    }
    
}
