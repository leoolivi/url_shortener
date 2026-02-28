package com.main.shortener.services.handlers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.main.shortener.domain.models.UrlMapping;
import com.main.shortener.services.UrlMappingService;
import com.urlshortener.data.request.mapping.GetMappingRequest;
import com.urlshortener.data.request.mapping.MappingRequest;
import com.urlshortener.data.response.mapping.MappingResponse;

import lombok.AllArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Component
@AllArgsConstructor
public class GetMappingRequestHandler implements MappingRequestHandler {

    private final UrlMappingService urlMappingService;
    private final ObjectMapper objectMapper;

    @Override
    public Class<?> getRequestClass() {
        return GetMappingRequest.class;
    }

    @Override
    public List<MappingResponse> handleRequest(MappingRequest req) {
        GetMappingRequest convertedRequest = objectMapper.convertValue(req, GetMappingRequest.class);
        UrlMapping mapping = urlMappingService.findByCode(convertedRequest.code());
        return List.of(objectMapper.convertValue(mapping, MappingResponse.class));
    }
    
}
