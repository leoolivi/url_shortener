package com.main.shortener.services.factory;

import org.springframework.stereotype.Component;

import com.main.shortener.exceptions.UnsupportedRequestTypeException;
import com.main.shortener.services.UrlMappingService;
import com.main.shortener.services.handlers.CreateMappingRequestHandler;
import com.main.shortener.services.handlers.DeleteMappingRequestHandler;
import com.main.shortener.services.handlers.GetAllMappingsRequestHandler;
import com.main.shortener.services.handlers.GetMappingRequestHandler;
import com.main.shortener.services.handlers.MappingRequestHandler;
import com.main.shortener.services.handlers.UpdateMappingRequestHandler;
import com.urlshortener.data.request.mapping.CreateMappingRequest;
import com.urlshortener.data.request.mapping.DeleteMappingRequest;
import com.urlshortener.data.request.mapping.GetAllMappingsRequest;
import com.urlshortener.data.request.mapping.GetMappingRequest;
import com.urlshortener.data.request.mapping.UpdateMappingRequest;

import lombok.AllArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Component
@AllArgsConstructor
public class MappingRequestHandlerFactory {

    private final UrlMappingService urlMappingService;
    private final ObjectMapper objectMapper;

    public MappingRequestHandler getHandler(Class<?> reqClass) {
        if (reqClass.equals(CreateMappingRequest.class)) {
            return new CreateMappingRequestHandler(urlMappingService, objectMapper);
        } else if (reqClass.equals(UpdateMappingRequest.class)) {
            return new UpdateMappingRequestHandler(urlMappingService, objectMapper);
        } else if (reqClass.equals(DeleteMappingRequest.class)) {
            return new DeleteMappingRequestHandler(urlMappingService, objectMapper);
        } else if (reqClass.equals(GetAllMappingsRequest.class)) {
            return new GetAllMappingsRequestHandler(urlMappingService, objectMapper);
        } else if (reqClass.equals(GetMappingRequest.class)) {
            return new GetMappingRequestHandler(urlMappingService, objectMapper);
        } else {
            throw new UnsupportedRequestTypeException("Unsupported request type: "+reqClass.getName());
        }
    }
}
