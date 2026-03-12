package com.main.auth.services;

import org.springframework.stereotype.Service;

import com.main.auth.exceptions.UnsupportedRequestTypeException;
import com.urlshortener.data.request.keys.KeyEvent;
import com.urlshortener.data.request.keys.UpdatedKeyEvent;

import lombok.AllArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Service
@AllArgsConstructor
public class KeyEventHandlerFactory {
    
    private final InternalKeyHolder internalKeyHolder;
    private final ObjectMapper objectMapper;

    public KeyEventHandler getKeyEventHandler(Class<? extends KeyEvent> eventType) {
        if (eventType.equals(UpdatedKeyEvent.class)) {
            return new UpdatedKeyEventHandler(internalKeyHolder, objectMapper);
        } else {
            throw new UnsupportedRequestTypeException("Unsupported event type: "+eventType.getName());
        }
    }    
}
