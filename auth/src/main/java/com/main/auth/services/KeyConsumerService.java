package com.main.auth.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.urlshortener.data.MessageEnvelope;
import com.urlshortener.data.request.keys.KeyRequest;
import com.urlshortener.data.response.keys.KeyResponse;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Service
@AllArgsConstructor
@Slf4j
public class KeyConsumerService {

    private final ObjectMapper objectMapper;
    private final KeyRequestHandlerFactory keyRequestHandlerFactory;
    private final ProducerService producerService;

    @RabbitListener(queues="gateway.keys.queue")
    public void handleKeyRequest(MessageEnvelope<KeyRequest> message) {
        KeyRequest requestPayload = objectMapper.convertValue(message.getPayload(), message.getPayloadType());
        KeyRequestHandler keyRequestHandler = keyRequestHandlerFactory.getRequestHandler(message.getPayloadType());
        KeyResponse responsePayload = keyRequestHandler.handleRequest(requestPayload);

        MessageEnvelope<KeyResponse> response = new MessageEnvelope<>();

        response.setCorrelationId(message.getCorrelationId());
        response.setMessageType("KEY_RESPONSE");
        response.setSource("auth");
        response.setToken(message.getToken());
        response.setTimestamp(System.currentTimeMillis());
        response.setPayload(responsePayload);

        log.info("Sent message:  {}", response);
        
        producerService.sendMessage("auth.keys.exchange", "key.response", response); 
    }
}
