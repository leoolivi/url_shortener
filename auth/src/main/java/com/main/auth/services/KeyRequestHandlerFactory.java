package com.main.auth.services;

import org.springframework.stereotype.Service;

import com.main.auth.exceptions.UnsupportedRequestTypeException;
import com.urlshortener.data.request.keys.GetUserPublicKey;
import com.urlshortener.data.request.keys.KeyRequest;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class KeyRequestHandlerFactory {

    private final UserKeyPairService keyPairService;

    public KeyRequestHandler getRequestHandler(Class<? extends KeyRequest> requestType) throws UnsupportedRequestTypeException {
        if (requestType.equals(GetUserPublicKey.class)) {
            return new GetUserPublicKeyRequestHandler(keyPairService);
        } else {
            throw new UnsupportedRequestTypeException("Unsupported request type:" + requestType.getName());
        }
    }
}
