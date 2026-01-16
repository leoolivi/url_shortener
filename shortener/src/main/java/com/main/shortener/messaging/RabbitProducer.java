package com.main.shortener.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RabbitProducer {
    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(String msg) {
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY, msg);
    }

    public void sendObject(Object obj) {
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RoutingKey.GENERIC.name(), obj);
    }
}
