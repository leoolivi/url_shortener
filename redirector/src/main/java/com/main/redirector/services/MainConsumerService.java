package com.main.redirector.services;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.urlshortener.data.MessageEnvelope;
import com.urlshortener.data.Request;
import com.urlshortener.data.Response;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
@AllArgsConstructor
@RabbitListener(queues = "redirector.queue")
public class MainConsumerService {

    private final RabbitTemplate rabbitTemplate;
    private final RedirectRequestHandlerFactory requestHandlerFactory;
    private final ObjectMapper objectMapper;

    @RabbitHandler
    public void handleMessage(MessageEnvelope<?> message) {
        log.info("Consumed message:  {}", message);

        Request requestPayload = (Request) objectMapper.convertValue(message.getPayload(), message.getPayloadType());
        
        try {
            RequestHandler requestHandler = requestHandlerFactory.getRequestHandlerByMessageType(message.getMessageType());
            Response responsePayload = requestHandler.handleRequest(requestPayload);

            var response = new MessageEnvelope<Response>();
            response.setCorrelationId(message.getCorrelationId());
            response.setMessageType(requestHandler.getRoutingKey().toUpperCase().replace(" ", "_"));
            response.setSource("redirector");
            response.setTimestamp(System.currentTimeMillis());
            response.setPayload(responsePayload);
    
            rabbitTemplate.convertAndSend("reply.redirector.exchange", requestHandler.getRoutingKey(), response);
            log.info("Sent message:  {}", responsePayload);
            
            

            } catch (RuntimeException e) {
                log.error(e.getMessage());
            }
    }
}
