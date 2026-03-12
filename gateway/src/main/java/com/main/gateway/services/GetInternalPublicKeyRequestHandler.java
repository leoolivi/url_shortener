package com.main.gateway.services;

import org.springframework.stereotype.Service;

import com.urlshortener.data.request.keys.GetInternalPublicKey;
import com.urlshortener.data.request.keys.KeyRequest;
import com.urlshortener.data.response.keys.InternalPublicKeyResponse;
import com.urlshortener.data.response.keys.KeyResponse;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GetInternalPublicKeyRequestHandler implements KeyRequestHandler {

    private final InternalKeyPairService keyPairService;

    @Override
    public Class<?> getRequestClass() {
        return GetInternalPublicKey.class;
    }

    @Override
    public KeyResponse handleRequest(KeyRequest request) {
        return new InternalPublicKeyResponse(keyPairService.findCurrentPublicKey());
    }
    
}
