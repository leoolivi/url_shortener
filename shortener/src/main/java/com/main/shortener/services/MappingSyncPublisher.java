package com.main.shortener.services;

import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.main.shortener.domain.models.UrlMapping;
import com.urlshortener.data.MessageEnvelope;
import com.urlshortener.data.request.mapping.CreateMappingRequest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class MappingSyncPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishCreated(UrlMapping mapping) {
        MessageEnvelope<CreateMappingRequest> event = new MessageEnvelope<>();
        event.setCorrelationId(UUID.randomUUID().toString());
        event.setMessageType("MAPPING_CREATED");
        event.setPayload(new CreateMappingRequest(mapping.getOriginalUrl(), mapping.getCode(), mapping.getUserId()));
        event.setPayloadType(CreateMappingRequest.class);
        event.setSource("shortener");
        event.setTimestamp(System.currentTimeMillis());
        rabbitTemplate.convertAndSend("data.sync.exchange", "sync.*", event);
        log.info("Created mapping created event on sync exchange");
    }
}
