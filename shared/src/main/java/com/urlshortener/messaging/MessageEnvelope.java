package com.urlshortener.messaging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class MessageEnvelope<T> {
    private String correlationId;
    private String messageType;
    private String source;
    private long timestamp;
    private T payload;
    private Class<T> payloadType;

    public MessageEnvelope(String correlationId, String messageType, String source, long timestamp, T payload) {
        this.correlationId = correlationId;
        this.messageType = messageType;
        this.source = source;
        this.timestamp = timestamp;
        this.payload = payload;
        this.payloadType = (Class<T>) payload.getClass();
    }

    public void setPayload(T payload) {
        this.payload = payload;
        this.payloadType = (Class<T>) payload.getClass();
    }
}
