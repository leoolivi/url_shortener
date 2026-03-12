package com.main.gateway.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.gateway.domain.events.ResponseFromServicesEvent;
import com.urlshortener.data.MessageEnvelope;
import com.urlshortener.data.request.keys.KeyRequest;
import com.urlshortener.data.response.keys.KeyResponse;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Service
@Slf4j
public class ConsumerService {
    
    private final ApplicationEventPublisher publisher;
    private final ProducerService producerService;
    private final ObjectMapper objectMapper;
    private final KeyRequestHandlerFactory requestHandlerFactory;
    
    @RabbitListener(queues = "reply.shortener.queue")
    public void handleResponseFromShortener(MessageEnvelope<?> message) {
        var event = new ResponseFromServicesEvent(message, "Returned mapping", message.getMessageType());
        log.info("Publishing event: {}", event);
        publisher.publishEvent(event);
    }

    @RabbitListener(queues = "reply.redirector.queue")
    public void handleResponseFromRedirector(MessageEnvelope<?> message) {
        var event = new ResponseFromServicesEvent(message, "Redirected to", message.getPayload().toString());
        log.info("Publishing event: {}", event);
        publisher.publishEvent(event);
    }

    @RabbitListener(queues="auth.keys.queue")
    public void handleKeyFromAuth(MessageEnvelope<KeyRequest> message) {
        KeyRequest requestPayload = objectMapper.convertValue(message.getPayload(), message.getPayloadType());
        KeyRequestHandler requestHandler = requestHandlerFactory.getRequestHandler(requestPayload.getClass());
        KeyResponse responsePayload = requestHandler.handleRequest(requestPayload);

        MessageEnvelope<KeyResponse> response = new MessageEnvelope<>();

        response.setCorrelationId(message.getCorrelationId());
        response.setMessageType("KEY_RESPONSE");
        response.setSource("gateway");
        response.setToken(message.getToken());
        response.setTimestamp(System.currentTimeMillis());
        response.setPayload(responsePayload);

        log.info("Sent message:  {}", response);
        
        producerService.sendMessage("gateway.keys.exchange", "key.response", response);
    }

}
