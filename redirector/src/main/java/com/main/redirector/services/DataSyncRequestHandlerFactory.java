package com.main.redirector.services;

import org.springframework.stereotype.Component;

import com.urlshortener.data.request.mapping.CreateMappingRequest;
import com.urlshortener.data.request.mapping.DeleteMappingRequest;
import com.urlshortener.data.request.mapping.UpdateMappingRequest;
import com.urlshortener.data.request.redirect.RedirectRequest;

import lombok.AllArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Component
@AllArgsConstructor
public class DataSyncRequestHandlerFactory {

    private final UrlMappingService urlMappingService;
    private final ObjectMapper objectMapper;

    public RequestHandler getRequestHandler(Class<?> reqType) {
        
        if (reqType == CreateMappingRequest.class) {
            return new CreateMappingRequestHandler(urlMappingService, objectMapper);
        } else if (reqType == DeleteMappingRequest.class){
            return new DeleteMappingRequestHandler(urlMappingService, objectMapper);
        } else if (reqType == UpdateMappingRequest.class){
            return new UpdateMappingRequestHandler(urlMappingService, objectMapper);
        } else if (reqType == RedirectRequest.class){
            return new RedirectRequestHandler(urlMappingService, objectMapper);
        }

        throw new AssertionError();
    }
}
