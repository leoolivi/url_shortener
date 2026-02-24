package com.main.redirector.services;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.main.redirector.exceptions.UnsupportedRequestTypeException;
import com.urlshortener.data.MessageEnvelope;
import com.urlshortener.data.Request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
@AllArgsConstructor
@RabbitListener(queues = "data.sync.redirector.queue")
public class DataSyncConsumerService {
    
    private final DataSyncRequestHandlerFactory requestHandlerFactory;
    private final ObjectMapper objectMapper;

    @RabbitHandler
    public void handleMessage(MessageEnvelope<?> message) {
        log.info("Consumed sync message:  {}", message);
        
        try {
            RequestHandler requestHandler = requestHandlerFactory.getRequestHandler(message.getPayloadType());

            Object rawPayload = message.getPayload();
            if (rawPayload == null) {
                log.warn("Sync message has null payload, type={}", message.getMessageType());
                return;
            }

            Request payload = (Request) objectMapper.convertValue(rawPayload, message.getPayloadType());
            log.info("Sync applied for type={}", message.getMessageType());

            requestHandler.handleRequest(payload);
            log.info("Sent message.");
        } catch (UnsupportedRequestTypeException e) {
            log.warn("Unsupported sync message: {}", e.getMessage());
        } catch (RuntimeException e) {
            log.error(e.getMessage());
        }
    }
}

