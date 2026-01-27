package com.main.auth.configuration;

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
    
    private final String REPLY_EXCHANGE = "reply.auth.exchange";

    private final String GATEWAY_EXCHANGE = "gateway.exchange";
    private final String GATEWAY_QUEUE = "auth.queue";
    private final String GATEWAY_ROUTING_KEY = "auth.*";

    @Bean
    public TopicExchange authReplyExchange() {
        return new TopicExchange(REPLY_EXCHANGE);
    }

    @Bean
    public TopicExchange gatewayExchange() {
        return new TopicExchange(GATEWAY_EXCHANGE);
    }


    @Bean
    public Queue gatewayQueue() {
        return new Queue(GATEWAY_QUEUE);
    }

    @Bean
    public Binding replyBinding(Queue gatewayQueue, TopicExchange gatewayExchange) {
        return BindingBuilder
                .bind(gatewayQueue)
                .to(gatewayExchange)
                .with(GATEWAY_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
