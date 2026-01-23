package com.urlshortener.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
@Builder
@NoArgsConstructor
public class MessageEnvelope<T> {
    private String correlationId;
    private String messageType;
    private String source;
    private long timestamp;
    private T payload;
    private Class<T> payloadType;

    public MessageEnvelope(String correlationId, String messageType, T payload, String source, long timestamp) {
        this.correlationId = correlationId;
        this.messageType = messageType;
        this.payload = payload;
        this.source = source;
        this.timestamp = timestamp;
        this.payloadType = (Class<T>) payload.getClass();
    }

    public void setPayload(T payload) {
        this.payload = payload;
        this.payloadType = (Class<T>) payload.getClass();
    }

    
}
