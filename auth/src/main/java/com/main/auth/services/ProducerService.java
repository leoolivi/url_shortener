package com.main.auth.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class ProducerService {
    
    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(String exchange, String routing_key, Object obj) {
        rabbitTemplate.convertAndSend(exchange, routing_key, obj);
        log.info("Message sent to exchange {} with routing key {}. Payload: {}", exchange, routing_key, obj);
    }

}
