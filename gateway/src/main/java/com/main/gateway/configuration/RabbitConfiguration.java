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
    
    private final String EXCHANGE = "gateway.exchange";

    private final String REPLY_SHORTENER_EXCHANGE = "reply.shortener.exchange";
    private final String REPLY_REDIRECTOR_EXCHANGE = "reply.redirector.exchange";
    private final String SHORTENER_QUEUE_RESPONSE = "reply.shortener.queue";
    private final String REDIRECTOR_QUEUE_RESPONSE = "reply.redirector.queue";
    private final String ROUTING_KEY = "mapping.*";


    @Bean
    public TopicExchange gatewayExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public TopicExchange replyShortenerExchange() {
        return new TopicExchange(REPLY_SHORTENER_EXCHANGE);
    }

    @Bean
    public TopicExchange replyRedirectorExchange() {
        return new TopicExchange(REPLY_REDIRECTOR_EXCHANGE);
    }

    @Bean
    public Queue replyShortenerQueue() {
        return new Queue(SHORTENER_QUEUE_RESPONSE);
    }

    @Bean
    public Queue replyRedirectorQueue() {
        return new Queue(REDIRECTOR_QUEUE_RESPONSE);
    }

    @Bean
    public Binding replyShortenerMappingBinding(Queue replyShortenerQueue, TopicExchange replyShortenerExchange) {
        return BindingBuilder
                .bind(replyShortenerQueue)
                .to(replyShortenerExchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public Binding replyRedirectorMappingBinding(Queue replyRedirectorQueue, TopicExchange replyRedirectorExchange) {
        return BindingBuilder
                .bind(replyRedirectorQueue)
                .to(replyRedirectorExchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
