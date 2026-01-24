package com.main.shortener.services;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.main.shortener.exceptions.MappingNotFoundException;
import com.urlshortener.messaging.CreateMappingRequest;
import com.urlshortener.messaging.ErrorResponse;
import com.urlshortener.messaging.MappingResponse;
import com.urlshortener.messaging.MessageEnvelope;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
@AllArgsConstructor
@RabbitListener(queues="shortener.queue")
public class ConsumerService {

    private final UrlMappingService service;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper mapper;
    
    @RabbitHandler
    public void handleCreateMapping(MessageEnvelope<?> message) {
        log.info("Consumed message:  {}", message);
        var payload = mapper.convertValue(message.getPayload(), CreateMappingRequest.class);
        try {
            var newMapping = service.createMapping(new CreateMappingRequest(payload.code(), payload.originalUrl(), payload.userId()));
            var responsePayload = new MappingResponse(newMapping.getId(), newMapping.getOriginalUrl(), newMapping.getCode());
    
            var response = new MessageEnvelope<MappingResponse>();
            response.setCorrelationId(message.getCorrelationId());
            response.setMessageType("MAPPING_RESPONSE");
            response.setSource("shortener");
            response.setTimestamp(System.currentTimeMillis());
            response.setPayload(responsePayload);
    
            rabbitTemplate.convertAndSend("reply.gateway.exchange", "mapping.response", response);
            log.info("Sent message:  {}", responsePayload);
        } catch (DataIntegrityViolationException e) { // Mapping Already Exists Exception
            var response = new MessageEnvelope<ErrorResponse>();
            response.setCorrelationId(message.getCorrelationId());
            response.setMessageType("MAPPING_ERROR");
            response.setSource("shortener");
            response.setTimestamp(System.currentTimeMillis());
            response.setPayload(new ErrorResponse("MAPPING_ALREADY_EXISTS", "Mapping already exists"));

            log.info("Sent message:  {}", response);
            
            rabbitTemplate.convertAndSend("reply.gateway.exchange", "mapping.error", response);
        } catch (MappingNotFoundException e) {
            var response = new MessageEnvelope<ErrorResponse>();
            response.setCorrelationId(message.getCorrelationId());
            response.setMessageType("MAPPING_ERROR");
            response.setSource("shortener");
            response.setTimestamp(System.currentTimeMillis());
            response.setPayload(new ErrorResponse("MAPPING_NOT_FOUND", e.getMessage()));

            log.info("Sent message:  {}", response);
            
            rabbitTemplate.convertAndSend("reply.gateway.exchange", "mapping.error", response);
        }
    }
}
