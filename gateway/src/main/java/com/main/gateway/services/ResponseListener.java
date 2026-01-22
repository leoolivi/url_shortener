package com.main.gateway.services;

import java.util.concurrent.CompletableFuture;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.main.gateway.domain.events.ResponseFromServicesEvent;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Service
@Getter @Setter
@NoArgsConstructor
public class ResponseListener {
    private CompletableFuture<ResponseFromServicesEvent> response = new CompletableFuture<>();

    public ResponseFromServicesEvent waitResponse() {
        var completed = response.join();
        response = new CompletableFuture<>();
        return completed;
    } 

    @EventListener
    public <T> void handleResponseEvent(ResponseFromServicesEvent event) {
        response.complete(event);
    }
}
