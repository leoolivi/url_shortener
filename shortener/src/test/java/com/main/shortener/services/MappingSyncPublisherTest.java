package com.main.shortener.services;

import com.main.shortener.domain.models.UrlMapping;
import com.main.shortener.services.messages.MappingSyncPublisher;
import com.urlshortener.data.MessageEnvelope;
import com.urlshortener.data.request.mapping.CreateMappingRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test suite per {@link MappingSyncPublisher}.
 *
 * Verifica che alla creazione di un mapping venga pubblicato sul
 * data.sync.exchange un messaggio con i dati corretti e un
 * correlationId generato automaticamente.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Shortener – MappingSyncPublisher")
class MappingSyncPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private MappingSyncPublisher publisher;

    @Test
    @DisplayName("publishCreated invia il messaggio su data.sync.exchange")
    void sendsToCorrectExchange() {
        UrlMapping mapping = new UrlMapping(1L, "code1", "https://example.com", 10L);

        publisher.publishCreated(mapping);

        verify(rabbitTemplate).convertAndSend(
                eq("data.sync.exchange"), eq("sync.*"), any(MessageEnvelope.class));
    }

    @Test
    @DisplayName("il payload del messaggio contiene i dati corretti del mapping")
    void payloadContainsMappingData() {
        UrlMapping mapping = new UrlMapping(1L, "code1", "https://example.com", 10L);

        publisher.publishCreated(mapping);

        ArgumentCaptor<MessageEnvelope> captor = ArgumentCaptor.forClass(MessageEnvelope.class);
        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), captor.capture());

        MessageEnvelope<CreateMappingRequest> sent = captor.getValue();
        assertThat(sent.getMessageType()).isEqualTo("MAPPING_CREATED");
        assertThat(sent.getSource()).isEqualTo("shortener");
        assertThat(sent.getCorrelationId()).isNotBlank();

        CreateMappingRequest payload = (CreateMappingRequest) sent.getPayload();
        assertThat(payload.code()).isEqualTo("code1");
        assertThat(payload.originalUrl()).isEqualTo("https://example.com");
        assertThat(payload.userId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("ogni chiamata genera un correlationId univoco")
    void generatesUniqueCorrelationIds() {
        UrlMapping mapping = new UrlMapping(1L, "code1", "https://example.com", 10L);

        publisher.publishCreated(mapping);
        publisher.publishCreated(mapping);

        ArgumentCaptor<MessageEnvelope> captor = ArgumentCaptor.forClass(MessageEnvelope.class);
        verify(rabbitTemplate, times(2)).convertAndSend(anyString(), anyString(), captor.capture());

        var ids = captor.getAllValues().stream()
                .map(MessageEnvelope::getCorrelationId)
                .toList();

        assertThat(ids.get(0)).isNotEqualTo(ids.get(1));
    }
}
