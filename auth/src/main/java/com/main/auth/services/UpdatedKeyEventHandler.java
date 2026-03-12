package com.main.auth.services;

import org.springframework.stereotype.Service;

import com.urlshortener.data.request.keys.KeyEvent;
import com.urlshortener.data.request.keys.UpdatedKeyEvent;

import lombok.AllArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Service
@AllArgsConstructor
public class UpdatedKeyEventHandler implements KeyEventHandler {

    private final InternalKeyHolder keyHolder;
    private final ObjectMapper objectMapper;

    @Override
    public Class<?> getEventType() {
        return UpdatedKeyEvent.class;
    }

    @Override
    public void handleEvent(KeyEvent event) {
        UpdatedKeyEvent convertedEvent = objectMapper.convertValue(event, UpdatedKeyEvent.class);
        keyHolder.setPublicKey(convertedEvent.getPublicKey());
    }

}
