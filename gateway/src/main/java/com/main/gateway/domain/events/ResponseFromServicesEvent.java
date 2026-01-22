package com.main.gateway.domain.events;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class ResponseFromServicesEvent extends ApplicationEvent {
    private final String message;
    private final String type;

    public ResponseFromServicesEvent(Object source, String message, String type) {
        super(source);
        this.message = message;
        this.type = type;
    }
}
