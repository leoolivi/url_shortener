package com.main.gateway.services;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.main.gateway.domain.events.ResponseFromServicesEvent;
import com.urlshortener.data.MessageEnvelope;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class ResponseListener {

    private CompletableFuture<MessageEnvelope<?>> response = new CompletableFuture<>();
    
    @Autowired
    private ObjectMapper mapper;

    public MessageEnvelope<?> waitComplete() {
        var complete = response.join();
        response = new CompletableFuture<>();
        return complete;
    }

    @EventListener
    public void onResponseEvent(ResponseFromServicesEvent event) {
        var payload = event.getSource();
        var message = mapper.convertValue(payload, MessageEnvelope.class);
        log.info("Received event message: {}", message.getSource());
        response.complete(message);
    }
}
