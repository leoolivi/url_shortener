package com.main.auth.services;

import com.urlshortener.data.request.keys.KeyEvent;

public interface KeyEventHandler {
    public Class<?> getEventType();
    public void handleEvent(KeyEvent event); 
}
