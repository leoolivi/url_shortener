package com.main.gateway.configuration;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@Getter @Setter
public class RabbitConfiguration {
    
    private String EXCHANGE = "main.exchange";

    @Bean
    TopicExchange mainExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public MessageConverter jsonConverter() {
        return new JacksonJsonMessageConverter("com.gateway.**");
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }
}
