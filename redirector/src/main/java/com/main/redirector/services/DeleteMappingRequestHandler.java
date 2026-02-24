package com.main.redirector.services;

import org.springframework.stereotype.Component;

import com.urlshortener.data.Request;
import com.urlshortener.data.request.mapping.DeleteMappingRequest;
import com.urlshortener.data.response.mapping.NoMappingResponse;

import lombok.AllArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Component
@AllArgsConstructor
public class DeleteMappingRequestHandler implements MappingRequestHandler {

    private final UrlMappingService urlMappingService;
    private final ObjectMapper objectMapper;

    @Override
    public Class<?> getRequestType() {
        return DeleteMappingRequest.class;
    }

    @Override
    public NoMappingResponse handleRequest(Request req) {
        DeleteMappingRequest convertedRequest = objectMapper.convertValue(req, DeleteMappingRequest.class);
        urlMappingService.deleteMapping(convertedRequest.code());
        return new NoMappingResponse();
    }

    @Override
    public String getRoutingKey() {
        return "mapping.delete";
    }
    
}
