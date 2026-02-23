package com.main.shortener.services;

import java.util.List;

import com.urlshortener.data.request.mapping.MappingRequest;
import com.urlshortener.data.response.mapping.MappingResponse;

public interface MappingRequestHandler {
    public Class<?> getRequestClass();
    public List<MappingResponse> handleRequest(MappingRequest request);
}
