package com.main.gateway.services;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.main.gateway.domain.events.ResponseFromServicesEvent;
import com.urlshortener.messaging.MappingResponse;
import com.urlshortener.messaging.MessageEnvelope;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RabbitListener(queues = "gateway.queue")
@AllArgsConstructor
@Service
@Slf4j
public class ConsumerService {

    private final ApplicationEventPublisher publisher;
    
    @RabbitHandler
    public void handleResponseFromServices(MessageEnvelope<MappingResponse> message) {
        var event = new ResponseFromServicesEvent(message, "Returned mapping", message.getMessageType());
        log.info("Publishing event: {}", event);
        publisher.publishEvent(event);
    }

}
