package com.main.gateway.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@Getter @Setter
public class RabbitConfiguration {
    
    private final String EXCHANGE = "main.exchange";

    private final String REPLY_EXCHANGE = "reply.gateway.exchange";
    private final String QUEUE_RESPONSE = "gateway.queue";
    private final String ROUTING_KEY = "mapping.response";

    @Bean
    public TopicExchange mainExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public TopicExchange replyExchange() {
        return new TopicExchange(REPLY_EXCHANGE);
    }

    @Bean
    public Queue replyShortenerMappingQueue() {
        return new Queue(QUEUE_RESPONSE);
    }

    @Bean
    public Binding replyShortenerMappingBinding(Queue replyShortenerMappingQueue, TopicExchange replyExchange) {
        return BindingBuilder
                .bind(replyShortenerMappingQueue)
                .to(replyExchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
