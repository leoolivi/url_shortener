package com.main.gateway.services;

import com.urlshortener.data.request.keys.KeyRequest;
import com.urlshortener.data.response.keys.KeyResponse;

public interface KeyRequestHandler {
    public Class<?> getRequestClass();
    public KeyResponse handleRequest(KeyRequest request);
}
