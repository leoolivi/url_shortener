package com.main.redirector.services;

import org.springframework.stereotype.Component;

import com.urlshortener.data.Request;
import com.urlshortener.data.request.mapping.UpdateMappingRequest;
import com.urlshortener.data.response.mapping.NoMappingResponse;

import lombok.AllArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Component
@AllArgsConstructor
public class UpdateMappingRequestHandler implements MappingRequestHandler {

    private final UrlMappingService urlMappingService;
    private final ObjectMapper objectMapper;

    @Override
    public Class<?> getRequestType() {
        return UpdateMappingRequest.class;
    }

    @Override
    public NoMappingResponse handleRequest(Request req) {
        UpdateMappingRequest convertedRequest = objectMapper.convertValue(req, UpdateMappingRequest.class);
        urlMappingService.updateMapping(convertedRequest);
        return new NoMappingResponse();
    }

    @Override
    public String getRoutingKey() {
        return "mapping.updated";
    }
    
}
