package com.main.shortener.services.handlers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.main.shortener.domain.models.UrlMapping;
import com.main.shortener.services.UrlMappingService;
import com.urlshortener.data.request.mapping.GetAllMappingsRequest;
import com.urlshortener.data.request.mapping.MappingRequest;
import com.urlshortener.data.response.mapping.MappingResponse;

import lombok.AllArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Component
@AllArgsConstructor
public class GetAllMappingsRequestHandler implements MappingRequestHandler {

    private final UrlMappingService urlMappingService;
    private final ObjectMapper objectMapper;

    @Override
    public Class<?> getRequestClass() {
        return GetAllMappingsRequest.class;
    }

    @Override
    public List<MappingResponse> handleRequest(MappingRequest req) {
        List<UrlMapping> mappings = urlMappingService.getMappings();
        List<MappingResponse> response = new ArrayList<>();
        for (UrlMapping mapping: mappings) {
            response.add(objectMapper.convertValue(mapping, MappingResponse.class));
        }
        return response;
    }
}
