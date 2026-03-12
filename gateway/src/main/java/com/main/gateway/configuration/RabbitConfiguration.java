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
    
    // Keys Exchange Config
    private final String GATEWAY_KEYS_EXCHANGE = "gateway.keys.exchange"; // Publishing
    private final String REPLY_AUTH_KEYS_EXCHANGE = "auth.keys.exchange"; // Consuming
    private final String REPLY_AUTH_KEYS_QUEUE = "auth.keys.queue";
    private final String KEY_ROUTING_KEY = "key.*";

    // General Purpose Exchanges Config
    private final String MAIN_EXCHANGE = "gateway.exchange";
    private final String REPLY_SHORTENER_EXCHANGE = "reply.shortener.exchange";
    private final String REPLY_REDIRECTOR_EXCHANGE = "reply.redirector.exchange";

    // General Purpose Queues Config
    private final String SHORTENER_QUEUE_RESPONSE = "reply.shortener.queue";
    private final String REDIRECTOR_QUEUE_RESPONSE = "reply.redirector.queue";
    
    // General Purpose Routing Keys
    private final String MAPPING_ROUTING_KEY = "mapping.*";
    private final String AUTH_ROUTING_KEY = "auth.*";
    private final String ERROR_ROUTING_KEY = "error.*";

    @Bean
    public TopicExchange gatewayExchange() {
        return new TopicExchange(MAIN_EXCHANGE);
    }

    @Bean
    public TopicExchange gatewayKeysExchange() {
        return new TopicExchange(GATEWAY_KEYS_EXCHANGE);
    }

    @Bean
    public TopicExchange replyAuthKeysExchange() {
        return new TopicExchange(REPLY_AUTH_KEYS_EXCHANGE);
    }

    @Bean
    public Queue replyAuthKeysQueue() {
        return new Queue(REPLY_AUTH_KEYS_QUEUE);
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
                .with(MAPPING_ROUTING_KEY);
    }

    @Bean
    public Binding replyShortenerErrorBinding(Queue replyShortenerQueue, TopicExchange replyShortenerExchange) {
        return BindingBuilder
                .bind(replyShortenerQueue)
                .to(replyShortenerExchange)
                .with(ERROR_ROUTING_KEY);
    }

    @Bean
    public Binding replyShortenerAuthBinding(Queue replyShortenerQueue, TopicExchange replyShortenerExchange) {
        return BindingBuilder
                .bind(replyShortenerQueue)
                .to(replyShortenerExchange)
                .with(AUTH_ROUTING_KEY);
    }

    @Bean
    public Binding replyRedirectorMappingBinding(Queue replyRedirectorQueue, TopicExchange replyRedirectorExchange) {
        return BindingBuilder
                .bind(replyRedirectorQueue)
                .to(replyRedirectorExchange)
                .with(MAPPING_ROUTING_KEY);
    }

    @Bean
    public Binding replyAuthKeyBinding(Queue replyAuthKeysQueue, TopicExchange replyAuthKeysExchange) {
        return BindingBuilder
                .bind(replyAuthKeysQueue)
                .to(replyAuthKeysExchange)
                .with(KEY_ROUTING_KEY);
    }

    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
