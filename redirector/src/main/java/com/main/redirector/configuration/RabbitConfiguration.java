package com.main.redirector.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {
    
    private final String REPLY_EXCHANGE_NAME = "reply.redirector.exchange";

    private final String GATEWAY_EXCHANGE_NAME = "gateway.exchange";
    private final String QUEUE_NAME = "redirector.queue";
    private final String ROUTING_KEY = "mapping.*";
    
    @Bean
    public TopicExchange gatewayExchange() {
        return new TopicExchange(GATEWAY_EXCHANGE_NAME);
    }

    @Bean
    public TopicExchange replyExchange() {
        return new TopicExchange(REPLY_EXCHANGE_NAME);
    }

    @Bean
    public Queue redirectorQueue() {
        return new Queue(QUEUE_NAME);
    }

    @Bean
    public Binding binding(Queue redirectorQueue, TopicExchange gatewayExchange) {
        return BindingBuilder
            .bind(redirectorQueue)
            .to(gatewayExchange)
            .with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonConverter() {
        return new JacksonJsonMessageConverter();
    }

}
