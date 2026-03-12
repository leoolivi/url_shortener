package com.main.auth.services;

import com.urlshortener.data.request.keys.KeyRequest;
import com.urlshortener.data.response.keys.KeyResponse;

public interface KeyRequestHandler {
    Class<?> getRequestClass();
    KeyResponse handleRequest(KeyRequest request);    
}
