package com.main.auth.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class RabbitConfiguration {

    private final String AUTH_KEYS_EXCHANGE = "auth.keys.exchange"; // Publishing
    private final String GATEWAY_KEYS_EXCHANGE = "gateway.keys.exchange"; // Consuming
    private final String GATEWAY_KEYS_QUEUE = "gateway.keys.queue";
    private final String KEY_ROUTING_KEY = "key.*";

    @Bean
    public TopicExchange gatewayKeysExchange() {
        return new TopicExchange(GATEWAY_KEYS_EXCHANGE);
    }

    @Bean
    public TopicExchange replyAuthKeysExchange() {
        return new TopicExchange(AUTH_KEYS_EXCHANGE);
    }

    @Bean
    public Queue keyGatewayQueue() {
        return new Queue(GATEWAY_KEYS_QUEUE);
    }

    @Bean
    public Binding keyGatewayExchangeBinding(Queue keyGatewayQueue, TopicExchange gatewayKeysExchange) {
        return BindingBuilder
                .bind(keyGatewayQueue)
                .to(gatewayKeysExchange)
                .with(KEY_ROUTING_KEY);
    }
}
