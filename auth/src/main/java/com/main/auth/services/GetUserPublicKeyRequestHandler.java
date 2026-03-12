package com.main.auth.services;

import org.springframework.stereotype.Service;

import com.urlshortener.data.request.keys.GetUserPublicKey;
import com.urlshortener.data.request.keys.KeyRequest;
import com.urlshortener.data.response.keys.KeyResponse;
import com.urlshortener.data.response.keys.UserPublicKeyResponse;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GetUserPublicKeyRequestHandler implements KeyRequestHandler {

    private final UserKeyPairService keyPairService;

    @Override
    public Class<?> getRequestClass() {
        return GetUserPublicKey.class;
    }

    @Override
    public KeyResponse handleRequest(KeyRequest request) {
        return new UserPublicKeyResponse(keyPairService.findCurrentPublicKey());
    }
    
}
