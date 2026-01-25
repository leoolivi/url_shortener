package com.main.redirector.services;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.main.redirector.exceptions.UnknownRequestPayloadType;
import com.urlshortener.messaging.CreateMappingRequest;
import com.urlshortener.messaging.DeleteMappingRequest;
import com.urlshortener.messaging.GetMappingRequest;
import com.urlshortener.messaging.MessageEnvelope;
import com.urlshortener.messaging.RedirectRequest;
import com.urlshortener.messaging.RedirectResponse;
import com.urlshortener.messaging.UpdateMappingRequest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
@AllArgsConstructor
@RabbitListener(queues = "redirector.queue")
public class ConsumerService {

    private final UrlMappingService service;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper mapper;

    @RabbitHandler
    public void handleMessage(MessageEnvelope<?> message) {
        log.info("Consumed message:  {}", message);
        RedirectResponse responsePayload = new RedirectResponse(null, null);
        
        try {
            if (message.getPayloadType().equals(CreateMappingRequest.class)) {
                var payload = mapper.convertValue(message.getPayload(), CreateMappingRequest.class);
                service.createMapping(new com.main.redirector.domain.data.CreateMappingRequest(payload.code(), payload.originalUrl()));
            } else if (message.getPayloadType().equals(DeleteMappingRequest.class)) {
                var payload = mapper.convertValue(message.getPayload(), DeleteMappingRequest.class);
                service.deleteMapping(payload.code());
            } else if (message.getPayloadType().equals(UpdateMappingRequest.class)) {
                var payload = mapper.convertValue(message.getPayload(), UpdateMappingRequest.class);
                service.updateMapping(new com.main.redirector.domain.data.UpdateMappingRequest(payload.code(), payload.originalUrl()));
            } else if (message.getPayloadType().equals(RedirectRequest.class)) {
                var payload = mapper.convertValue(message.getPayload(), GetMappingRequest.class);
                var originalUrl = service.getOriginalUrl(payload.code());
                responsePayload = new RedirectResponse(payload.code(), originalUrl);
            } else {
                throw new UnknownRequestPayloadType("Unknown request payload type.");
            }
            
            var response = new MessageEnvelope<RedirectResponse>();
            response.setCorrelationId(message.getCorrelationId());
            response.setMessageType("MAPPING_REDIRECT");
            response.setSource("shortener");
            response.setTimestamp(System.currentTimeMillis());
            response.setPayload(responsePayload);
    
            rabbitTemplate.convertAndSend("reply.redirector.exchange", "mapping.redirect", response);
            log.info("Sent message:  {}", responsePayload);

            } catch (RuntimeException e) {
                log.error(e.getMessage());
            }
    }
}
