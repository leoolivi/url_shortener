package com.main.gateway.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.main.gateway.domain.events.ResponseFromServicesEvent;
import com.urlshortener.data.MessageEnvelope;
import com.urlshortener.data.response.redirect.RedirectResponse;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Service
@Slf4j
public class ConsumerService {
    
    private final ApplicationEventPublisher publisher;
    
    @RabbitListener(queues = "reply.shortener.queue")
    public void handleResponseFromShortener(MessageEnvelope<?> message) {
        var event = new ResponseFromServicesEvent(message, "Returned mapping", message.getMessageType());
        log.info("Publishing event: {}", event);
        publisher.publishEvent(event);
    }

    @RabbitListener(queues = "reply.redirector.queue")
    public void handleResponseFromRedirector(MessageEnvelope<RedirectResponse> message) {
        var event = new ResponseFromServicesEvent(message, "Redirected to", message.getPayload().toString());
        log.info("Publishing event: {}", event);
        publisher.publishEvent(event);
    }

}
