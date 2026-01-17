package com.main.shortener.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.main.shortener.configuration.RabbitConfiguration;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RabbitProducer {
    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(String msg) {
        rabbitTemplate.convertAndSend(RabbitConfiguration.EXCHANGE, RabbitConfiguration.ROUTING_KEY, msg);
    }

    public void sendObject(Object obj) {
        rabbitTemplate.convertAndSend(RabbitConfiguration.EXCHANGE, RoutingKey.GENERIC.name(), obj);
    }

    public void sendObject(String exchange, String routingKey, Object obj) {
        rabbitTemplate.convertAndSend(RabbitConfiguration.EXCHANGE, routingKey, obj);
    }

    public void sendObject(Object obj, String routingKey) {
        rabbitTemplate.convertAndSend(RabbitConfiguration.EXCHANGE, routingKey, obj);
    }
}
