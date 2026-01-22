package com.main.gateway.services;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.main.gateway.domain.events.ResponseFromServicesEvent;
import com.urlshortener.messaging.MessageEnvelope;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@RabbitListener(queues = "gateway.queue")
@AllArgsConstructor
@Service
@Slf4j
public class ConsumerService {
    
    private final ApplicationEventPublisher publisher;
    private final ObjectMapper mapper;

    @RabbitHandler
    public <T>void handleResponse(MessageEnvelope<T> request) {
        Class<T> payloadType = request.getPayloadType();
        var payload = mapper.convertValue(request.getPayload(), payloadType);
        request.setPayload(payload);
        var event = new ResponseFromServicesEvent(request, "Received Message", request.getMessageType());
        log.info("Publishing event: {}", event);
        publisher.publishEvent(event);
    }

}
