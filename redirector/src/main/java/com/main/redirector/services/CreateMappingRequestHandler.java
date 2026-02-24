package com.main.redirector.services;

import org.springframework.stereotype.Component;

import com.urlshortener.data.Request;
import com.urlshortener.data.request.mapping.CreateMappingRequest;
import com.urlshortener.data.response.mapping.NoMappingResponse;

import lombok.AllArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Component
@AllArgsConstructor
public class CreateMappingRequestHandler implements MappingRequestHandler {

    private final UrlMappingService urlMappingService;
    private final ObjectMapper objectMapper;

    @Override
    public Class<?> getRequestType() {
        return CreateMappingRequest.class;
    }

    @Override
    public NoMappingResponse handleRequest(Request request) {
        CreateMappingRequest castedReq = objectMapper.convertValue(request, CreateMappingRequest.class);
        urlMappingService.createMapping(castedReq);
        return new NoMappingResponse(); 
    }

    @Override
    public String getRoutingKey() {
        return "mapping.created";
    }
    
}
