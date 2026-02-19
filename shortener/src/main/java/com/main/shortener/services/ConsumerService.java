package com.main.shortener.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.main.shortener.domain.models.UrlMapping;
import com.main.shortener.exceptions.MappingAlreadyExistException;
import com.main.shortener.exceptions.MappingNotFoundException;
import com.main.shortener.exceptions.UnknownRequestPayloadType;
import com.urlshortener.data.CreateMappingRequest;
import com.urlshortener.data.DeleteMappingRequest;
import com.urlshortener.data.ErrorResponse;
import com.urlshortener.data.GetAllMappingsRequest;
import com.urlshortener.data.GetMappingRequest;
import com.urlshortener.data.MappingResponse;
import com.urlshortener.data.MessageEnvelope;
import com.urlshortener.data.UpdateMappingRequest;
import com.urlshortener.data.User;
import com.urlshortener.security.JwtVerifier;

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
    private final JwtVerifier jwtVerifier;
    
    @RabbitHandler
    public void handleMessage(MessageEnvelope<?> message) {
        log.info("Consumed message:  {}", message);
        List<MappingResponse> responsePayload = new ArrayList<>();
        
        try {
            User userDetails = jwtVerifier.extractUser(message.getToken());
            if (message.getPayloadType().equals(CreateMappingRequest.class)) {
                var payload = mapper.convertValue(message.getPayload(), CreateMappingRequest.class);
                var newMapping = service.createMapping(new CreateMappingRequest(payload.originalUrl(), payload.code()), userDetails.getId());
                responsePayload.add(new MappingResponse(newMapping.getId(), newMapping.getOriginalUrl(), newMapping.getCode(), newMapping.getUserId()));
            } else if (message.getPayloadType().equals(DeleteMappingRequest.class)) {
                var payload = mapper.convertValue(message.getPayload(), DeleteMappingRequest.class);
                var deletedMapping = service.deleteMappingByCode(payload.code());
                responsePayload.add(new MappingResponse(deletedMapping.getId(), deletedMapping.getOriginalUrl(), deletedMapping.getCode(), deletedMapping.getUserId()));
            } else if (message.getPayloadType().equals(UpdateMappingRequest.class)) {
                var payload = mapper.convertValue(message.getPayload(), UpdateMappingRequest.class);
                var updatedMapping = service.updateMapping(payload);
                responsePayload.add(new MappingResponse(updatedMapping.getId(), updatedMapping.getOriginalUrl(), updatedMapping.getCode(), updatedMapping.getUserId()));
            } else if (message.getPayloadType().equals(GetMappingRequest.class)) {
                var payload = mapper.convertValue(message.getPayload(), GetMappingRequest.class);
                var mapping = service.findByCode(payload.code());
                responsePayload.add(new MappingResponse(mapping.getId(), mapping.getOriginalUrl(), mapping.getCode(), mapping.getUserId()));
            } else if (message.getPayloadType().equals(GetAllMappingsRequest.class)) {
                var mappings = service.getMappings();
                for (UrlMapping mapping: mappings) {
                    responsePayload.add(new MappingResponse(mapping.getId(), mapping.getOriginalUrl(), mapping.getCode(), mapping.getUserId()));
                }
            } else {
                throw new UnknownRequestPayloadType("Unknown request payload type.");
            }
            
            var response = new MessageEnvelope<List<MappingResponse>>();
            response.setCorrelationId(message.getCorrelationId());
            response.setMessageType("MAPPING_RESPONSE");
            response.setSource("shortener");
            response.setToken(message.getToken());
            response.setTimestamp(System.currentTimeMillis());
            response.setPayload(responsePayload);
    
            rabbitTemplate.convertAndSend("reply.shortener.exchange", "mapping.response", response);
            log.info("Sent message:  {}", responsePayload);

            } catch (MappingAlreadyExistException e) { // Mapping Already Exists Exception
                var response = new MessageEnvelope<ErrorResponse>();
                response.setCorrelationId(message.getCorrelationId());
                response.setMessageType("MAPPING_ERROR");
                response.setSource("shortener");
                response.setToken(message.getToken());
                response.setTimestamp(System.currentTimeMillis());
                response.setPayload(new ErrorResponse("MAPPING_ALREADY_EXISTS", "Mapping already exists"));
    
                log.info("Sent message:  {}", response);
                
                rabbitTemplate.convertAndSend("reply.shortener.exchange", "mapping.error", response);
            } catch (MappingNotFoundException e) { // Mapping not found Exception
                var response = new MessageEnvelope<ErrorResponse>();
                response.setCorrelationId(message.getCorrelationId());
                response.setMessageType("MAPPING_ERROR");
                response.setSource("shortener");
                response.setToken(message.getToken());
                response.setTimestamp(System.currentTimeMillis());
                response.setPayload(new ErrorResponse("MAPPING_NOT_FOUND", e.getMessage()));
    
                log.info("Sent message:  {}", response);
                
                rabbitTemplate.convertAndSend("reply.shortener.exchange", "mapping.error", response);
            } catch (UnknownRequestPayloadType e) {
                var response = new MessageEnvelope<ErrorResponse>();
                response.setCorrelationId(message.getCorrelationId());
                response.setMessageType("AUTH_ERROR");
                response.setSource("shortener");
                response.setToken(message.getToken());
                response.setTimestamp(System.currentTimeMillis());
                response.setPayload(new ErrorResponse("UNKNOWN_REQUEST_PAYLOAD_TYPE", e.getMessage()));
    
                log.info("Sent message:  {}", response);
                
                rabbitTemplate.convertAndSend("reply.shortener.exchange", "auth.error", response);
            } catch (IllegalArgumentException e) {
                var response = new MessageEnvelope<ErrorResponse>();
                response.setCorrelationId(message.getCorrelationId());
                response.setMessageType("AUTH_ERROR");
                response.setSource("shortener");
                response.setToken(message.getToken());
                response.setTimestamp(System.currentTimeMillis());
                response.setPayload(new ErrorResponse("ILLEGAL_ARGUMENT", e.getMessage()));
    
                log.info("Sent message:  {}", response);
                
                rabbitTemplate.convertAndSend("reply.shortener.exchange", "auth.error", response);
            } catch (Exception e) {
                var response = new MessageEnvelope<ErrorResponse>();
                response.setCorrelationId(message.getCorrelationId());
                response.setMessageType("SERVER_ERROR");
                response.setSource("shortener");
                response.setToken(message.getToken());
                response.setTimestamp(System.currentTimeMillis());
                response.setPayload(new ErrorResponse("GENERAL_ERROR", e.getMessage()));
    
                log.info("Sent message:  {}", response);
                
                rabbitTemplate.convertAndSend("reply.shortener.exchange", "general.error", response);
            }
    }
}
