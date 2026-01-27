package com.main.auth.services;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import com.urlshortener.messaging.AuthenticateRequest;
import com.urlshortener.messaging.AuthenticateResponse;
import com.urlshortener.messaging.ErrorResponse;
import com.urlshortener.messaging.MessageEnvelope;
import com.urlshortener.messaging.RegisterRequest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Service
@AllArgsConstructor
@RabbitListener(queues = "auth.queue")
@Slf4j
public class ConsumerService {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper mapper;
    private final AuthenticationService authService;

    @RabbitHandler
    public void handleMessage(MessageEnvelope<?> message) {
        log.info("Received message");
        AuthenticateResponse responsePayload;
        try {
            if (message.getPayloadType().equals(AuthenticateRequest.class)) {
                var payload = mapper.convertValue(message.getPayload(), AuthenticateRequest.class);
                responsePayload = authService.authenticate(payload);
            } else if (message.getPayload() instanceof RegisterRequest) {
                var payload = mapper.convertValue(message.getPayload(), RegisterRequest.class);
                responsePayload = authService.register(payload);
            } else {
                log.info("Unsupported request type: {}", message.getPayloadType());
                return;
            }
            
            var response = new MessageEnvelope<AuthenticateResponse>();
            response.setCorrelationId(message.getCorrelationId());
            response.setMessageType("AUTH_RESPONSE");
            response.setSource("auth");
            response.setTimestamp(System.currentTimeMillis());
            response.setPayload(responsePayload);
    
            rabbitTemplate.convertAndSend("reply.auth.exchange", "auth.response", response);
            log.info("Sent message:  {}", responsePayload);

        } catch (BadCredentialsException e) {
            var response = new MessageEnvelope<ErrorResponse>();
                response.setCorrelationId(message.getCorrelationId());
                response.setMessageType("AUTH_ERROR");
                response.setSource("auth");
                response.setTimestamp(System.currentTimeMillis());
                response.setPayload(new ErrorResponse("INVALID_CREDENTIALS", e.getMessage()));
    
                log.info("Sent message:  {}", response);
                
                rabbitTemplate.convertAndSend("reply.auth.exchange", "auth.error", response);
        }
        

    }

}