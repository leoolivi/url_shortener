package com.main.gateway.services;

import java.util.HashMap;
import java.util.Map;
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

    private Map<String, CompletableFuture<MessageEnvelope<?>>> requests = new HashMap<>();
    
    @Autowired
    private ObjectMapper mapper;

    public MessageEnvelope<?> waitComplete(String correlationId) {
        var complete = requests.get(correlationId).join();
        return complete;
    }

    public void addCompletableRequest(String correlationId) {
        requests.put(correlationId, new CompletableFuture<>());
    }

    @EventListener
    public void onResponseEvent(ResponseFromServicesEvent event) {
        var payload = event.getSource();
        var message = mapper.convertValue(payload, MessageEnvelope.class);
        log.info("Received event message: {}", message.getSource());
        requests.get(message.getCorrelationId()).complete(message);
    }
}
