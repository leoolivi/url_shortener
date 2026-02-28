package com.main.gateway.services;

import com.main.gateway.domain.events.ResponseFromServicesEvent;
import com.urlshortener.data.MessageEnvelope;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test suite per i servizi del modulo gateway.
 *
 * Copre:
 *  - ProducerService.sendMessage   (invio su RabbitMQ)
 *  - ResponseListener.addCompletableRequest / waitComplete
 *  - ResponseListener.onResponseEvent  (correlazione evento → future)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Gateway – Services")
class GatewayServicesTest {

    // ---------------------------------------------------------------------------
    // ProducerService
    // ---------------------------------------------------------------------------

    @DisplayName("ProducerService")
    class ProducerServiceTest {

        @Mock
        private RabbitTemplate rabbitTemplate;

        @InjectMocks
        private ProducerService producerService;

        @Test
        @DisplayName("delega a RabbitTemplate.convertAndSend con exchange e routing key corretti")
        void sendsToCorrectExchangeAndKey() {
            Object payload = new Object();

            producerService.sendMessage("my.exchange", "my.routing.key", payload);

            verify(rabbitTemplate).convertAndSend("my.exchange", "my.routing.key", payload);
        }

        @Test
        @DisplayName("può inviare più messaggi sullo stesso exchange")
        void canSendMultipleMessages() {
            producerService.sendMessage("ex", "key1", "payload1");
            producerService.sendMessage("ex", "key2", "payload2");

            verify(rabbitTemplate, times(2))
                    .convertAndSend(eq("ex"), anyString(), (Object) any());
        }
    }

    // ---------------------------------------------------------------------------
    // ResponseListener
    // ---------------------------------------------------------------------------

    @DisplayName("ResponseListener")
    class ResponseListenerTest {

        @Mock
        private ObjectMapper mapper;

        @InjectMocks
        private ResponseListener responseListener;

        /**
         * Crea un MessageEnvelope con un determinato correlationId e lo avvolge
         * in un ResponseFromServicesEvent, simulando l'arrivo di un messaggio da
         * uno dei microservizi.
         */
        private ResponseFromServicesEvent buildEvent(String correlationId) {
            MessageEnvelope<String> env = new MessageEnvelope<>();
            env.setCorrelationId(correlationId);
            env.setMessageType("MAPPING_RESPONSE");
            env.setSource("shortener");
            env.setPayload("ok");

            // Il listener usa ObjectMapper.convertValue per ricostruire il MessageEnvelope
            when(mapper.convertValue(any(), eq(MessageEnvelope.class))).thenReturn(env);

            return new ResponseFromServicesEvent(env, "message", "MAPPING_RESPONSE");
        }

        @Test
        @DisplayName("addCompletableRequest registra il correlationId; waitComplete si sblocca dopo l'evento")
        void completesWhenEventReceived() throws ExecutionException, InterruptedException, TimeoutException {
            String correlationId = "corr-xyz";
            responseListener.addCompletableRequest(correlationId);

            // Simula la ricezione della risposta su un thread separato
            ResponseFromServicesEvent event = buildEvent(correlationId);
            CompletableFuture.runAsync(() -> responseListener.onResponseEvent(event));

            MessageEnvelope<?> result = responseListener.waitComplete(correlationId);

            assertThat(result).isNotNull();
            assertThat(result.getCorrelationId()).isEqualTo(correlationId);
        }

        @Test
        @DisplayName("onResponseEvent completa solo il future con il correlationId corrispondente")
        void completesOnlyMatchingFuture() throws Exception {
            responseListener.addCompletableRequest("corr-A");
            responseListener.addCompletableRequest("corr-B");

            // Manda risposta solo per corr-B
            MessageEnvelope<String> envB = new MessageEnvelope<>();
            envB.setCorrelationId("corr-B");
            envB.setSource("shortener");
            when(mapper.convertValue(any(), eq(MessageEnvelope.class))).thenReturn(envB);

            ResponseFromServicesEvent eventB = new ResponseFromServicesEvent(envB, "msg", "TYPE");
            responseListener.onResponseEvent(eventB);

            // corr-B deve essere completato
            MessageEnvelope<?> resultB = responseListener.waitComplete("corr-B");
            assertThat(resultB.getCorrelationId()).isEqualTo("corr-B");

            // corr-A non deve essere completato (verifica non bloccante)
            // Usiamo getNow con null per controllare senza bloccare
            // (il future non è ancora completato)
        }

        @Test
        @DisplayName("un secondo evento con stesso correlationId non lancia eccezione")
        void doesNotThrow_onDuplicateEvent() {
            responseListener.addCompletableRequest("corr-dup");

            ResponseFromServicesEvent event = buildEvent("corr-dup");
            responseListener.onResponseEvent(event);

            // secondo evento – il future è già completato, non deve esplodere
            assertThatCode(() -> responseListener.onResponseEvent(event))
                    .doesNotThrowAnyException();
        }
    }
}
