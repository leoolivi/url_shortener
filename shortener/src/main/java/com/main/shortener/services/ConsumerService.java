package com.main.shortener.services;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.main.shortener.exceptions.MappingAlreadyExistException;
import com.main.shortener.exceptions.MappingNotFoundException;
import com.main.shortener.exceptions.UnknownRequestPayloadType;
import com.urlshortener.data.MessageEnvelope;
import com.urlshortener.data.request.mapping.MappingRequest;
import com.urlshortener.data.response.error.ErrorResponse;
import com.urlshortener.data.response.mapping.MappingResponse;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
@AllArgsConstructor
@RabbitListener(queues="shortener.queue")
public class ConsumerService {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper mapper;
    private final MappingRequestHandlerFactory mappingRequestHandlerFactory;
    
    @RabbitHandler
    public void handleMessage(MessageEnvelope<?> message) {
        log.info("Consumed message:  {}", message);

        MappingRequest requestPayload = (MappingRequest) mapper.convertValue(message.getPayload(), message.getPayloadType());
        
        try {
            MappingRequestHandler requestHandler = mappingRequestHandlerFactory.getHandler(message.getPayloadType());
            List<MappingResponse> responsePayload = requestHandler.handleRequest(requestPayload);

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
                
                rabbitTemplate.convertAndSend("reply.shortener.exchange", "error.internal", response);
            }
    }
}
