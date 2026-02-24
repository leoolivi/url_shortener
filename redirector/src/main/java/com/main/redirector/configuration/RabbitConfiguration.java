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
    private final String DATA_SYNC_EXCHANGE_NAME = "data.sync.exchange";

    private final String MAIN_QUEUE_NAME = "redirector.queue";
    private final String DATA_SYNC_QUEUE_NAME = "data.sync.redirector.queue";
    
    private final String REDIRECT_ROUTING_KEY = "redirect.*";
    private final String DATA_SYNC_ROUTING_KEY = "sync.*";
    
    @Bean
    public TopicExchange gatewayExchange() {
        return new TopicExchange(GATEWAY_EXCHANGE_NAME);
    }

    @Bean
    public TopicExchange dataSyncExchange() {
        return new TopicExchange(DATA_SYNC_EXCHANGE_NAME);
    }

    @Bean
    public TopicExchange replyExchange() {
        return new TopicExchange(REPLY_EXCHANGE_NAME);
    }

    @Bean
    public Queue redirectorQueue() {
        return new Queue(MAIN_QUEUE_NAME);
    }

    @Bean
    public Queue dataSyncQueue() {
        return new Queue(DATA_SYNC_QUEUE_NAME);
    }

    @Bean
    public Binding redirectorBinding(Queue redirectorQueue, TopicExchange gatewayExchange) {
        return BindingBuilder
            .bind(redirectorQueue)
            .to(gatewayExchange)
            .with(REDIRECT_ROUTING_KEY);
    }

    @Bean
    public Binding dataSyncBinding(Queue dataSyncQueue, TopicExchange dataSyncExchange) {
        return BindingBuilder
            .bind(dataSyncQueue)
            .to(dataSyncExchange)
            .with(DATA_SYNC_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonConverter() {
        return new JacksonJsonMessageConverter();
    }

}
