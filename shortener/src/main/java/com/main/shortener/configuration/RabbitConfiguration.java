package com.main.shortener.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.main.shortener.services.ConsumerService;

@Configuration
public class RabbitConfiguration {
    
    private final String QUEUE_NAME = "shortener.queue";
    private final String EXCHANGE_NAME = "main.exchange";
    private final String ROUTING_KEY = "mapping.*";

    @Bean
    Queue queue() {
        return new Queue(QUEUE_NAME);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    Binding mainBinding(Queue queue, TopicExchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(ROUTING_KEY);
    }

    @Bean
    SimpleMessageListenerContainer simpleMessageListenerContainer(ConnectionFactory connectionFactory, MessageListenerAdapter messageListenerAdapter) {
        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();
        simpleMessageListenerContainer.setConnectionFactory(connectionFactory);
        simpleMessageListenerContainer.setQueueNames(QUEUE_NAME);
        simpleMessageListenerContainer.setMessageListener(messageListenerAdapter);
        return simpleMessageListenerContainer;
    }

    @Bean
    MessageListenerAdapter messageListenerAdapter(ConsumerService consumerService) {
        return new MessageListenerAdapter(consumerService, "consumeMessage");
    }

    @Bean
    public MessageConverter jsonConverter() {
        return new JacksonJsonMessageConverter("com.shortener.**");
    }

}
