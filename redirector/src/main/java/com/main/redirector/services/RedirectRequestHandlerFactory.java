package com.main.redirector.services;

import org.springframework.stereotype.Component;

import com.main.redirector.exceptions.UnsupportedRequestTypeException;
import com.urlshortener.data.request.redirect.RedirectRequest;

import lombok.AllArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Component
@AllArgsConstructor
public class RedirectRequestHandlerFactory {

    private final UrlMappingService urlMappingService;
    private final ObjectMapper objectMapper;

    public RequestHandler getRequestHandlerByClass(Class<?> reqType) {
        
        if (reqType == RedirectRequest.class) {
            return new RedirectRequestHandler(urlMappingService, objectMapper);
        }
        throw new UnsupportedRequestTypeException("Unsupported request type: " +
                                                        (reqType != null ? reqType.getName() : "null"));
    }

    public RequestHandler getRequestHandlerByMessageType(String messageType) {
        return switch (messageType) {
            case "MAPPING_REDIRECT" -> getRequestHandlerByClass(RedirectRequest.class);
            default -> throw new UnsupportedRequestTypeException("Unsupported message type: "+messageType);
        };
    }
}
