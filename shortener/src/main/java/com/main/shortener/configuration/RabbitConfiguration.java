package com.main.shortener.configuration;

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
    
    private final String SHORTENER_EXCHANGE_NAME = "reply.shortener.exchange";
    private final String DATA_SYNC_EXCHANGE = "data.sync.exchange";
    private final String GATEWAY_EXCHANGE_NAME = "gateway.exchange";
    
    private final String MAPPING_ROUTING_KEY = "mapping.*";
    private final String DATA_SYNC_ROUTING_KEY = "sync.*";
    private final String DATA_SYNC_QUEUE_NAME = "data.sync.redirector.queue";
    private final String QUEUE_NAME = "shortener.queue";

    @Bean
    public TopicExchange shortenerExchange() {
        return new TopicExchange(SHORTENER_EXCHANGE_NAME);
    }

    @Bean
    public Queue gatewayQueue() {
        return new Queue(QUEUE_NAME);
    }

    @Bean
    public Queue dataSyncQueue() {
        return new Queue(DATA_SYNC_QUEUE_NAME, true);
    }

    @Bean
    public TopicExchange dataSyncExchange() {
        return new TopicExchange(DATA_SYNC_EXCHANGE);
    }

    @Bean
    public TopicExchange gatewayExchange() {
        return new TopicExchange(GATEWAY_EXCHANGE_NAME);
    }


    @Bean
    public Binding dataSyncBinding(Queue dataSyncQueue, TopicExchange dataSyncExchange) {
        return BindingBuilder
                .bind(dataSyncQueue)
                .to(dataSyncExchange)
                .with(DATA_SYNC_ROUTING_KEY);
    }

    @Bean
    public Binding mappingBinding(Queue gatewayQueue, TopicExchange gatewayExchange) {
        return BindingBuilder
                .bind(gatewayQueue)
                .to(gatewayExchange)
                .with(MAPPING_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        JacksonJsonMessageConverter jsonConverter = new JacksonJsonMessageConverter();
        return jsonConverter;
    }

}
