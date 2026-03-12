package com.main.gateway.services;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.gateway.exceptions.UnsupportedRequestTypeException;
import com.urlshortener.data.request.keys.GetInternalPublicKey;
import com.urlshortener.data.request.keys.KeyRequest;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class KeyRequestHandlerFactory {

    private final ObjectMapper objectMapper;
    private final InternalKeyPairService keyPairService;

    public KeyRequestHandler getRequestHandler(Class<? extends KeyRequest> requestType) {
        if (requestType.equals(GetInternalPublicKey.class)) {
            return new GetInternalPublicKeyRequestHandler(keyPairService);
        } else {
            throw new UnsupportedRequestTypeException("Unsupported request type:" + requestType.getName());
        }        
    }
}
