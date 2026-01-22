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
    
    private final String EXCHANGE_NAME = "main.exchange";
    private final String MAPPING_ROUTING_KEY = "mapping.*";
    private final String MAPPING_QUEUE_NAME = "shortener.mapping";

    @Bean
    public Queue mappingQueue() {
        return new Queue(MAPPING_QUEUE_NAME);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    Binding mappingBinding(Queue queue, TopicExchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(MAPPING_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        JacksonJsonMessageConverter jsonConverter = new JacksonJsonMessageConverter();
        return jsonConverter;
    }

}
