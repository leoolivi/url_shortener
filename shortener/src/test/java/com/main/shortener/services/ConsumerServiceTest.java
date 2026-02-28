package com.main.shortener.services;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.main.shortener.exceptions.MappingAlreadyExistException;
import com.main.shortener.exceptions.MappingNotFoundException;
import com.main.shortener.exceptions.UnknownRequestPayloadType;
import com.main.shortener.services.factory.MappingRequestHandlerFactory;
import com.main.shortener.services.handlers.MappingRequestHandler;
import com.main.shortener.services.messages.MainConsumerService;
import com.urlshortener.data.MessageEnvelope;
import com.urlshortener.data.request.mapping.CreateMappingRequest;
import com.urlshortener.data.response.error.ErrorResponse;
import com.urlshortener.data.response.mapping.MappingResponse;

import tools.jackson.databind.ObjectMapper;

/**
 * Test suite per {@link MainConsumerService} (shortener).
 *
 * Copre le risposte inviate su RabbitMQ in caso di:
 *  - elaborazione corretta → "reply.shortener.exchange"  con routing "mapping.response"
 *  - MappingAlreadyExistException  → routing "mapping.error"
 *  - MappingNotFoundException      → routing "mapping.error"
 *  - UnknownRequestPayloadType     → routing "auth.error"
 *  - IllegalArgumentException      → routing "auth.error"
 *  - Eccezione generica            → routing "error.internal"
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Shortener – ConsumerService")
class ConsumerServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private MappingRequestHandlerFactory mappingRequestHandlerFactory;

    @InjectMocks
    private MainConsumerService consumerService;

    /** Helper: costruisce un MessageEnvelope generico pronto per i test. */
    private MessageEnvelope<CreateMappingRequest> buildEnvelope() {
        MessageEnvelope<CreateMappingRequest> env = new MessageEnvelope<>();
        env.setCorrelationId("corr-123");
        env.setMessageType("MAPPING_CREATED");
        env.setSource("gateway");
        env.setTimestamp(System.currentTimeMillis());
        env.setPayload(new CreateMappingRequest("https://x.com", "code1", 1L));
        return env;
    }

    // ---------------------------------------------------------------------------
    // Percorso felice
    // ---------------------------------------------------------------------------

    @Nested
    @DisplayName("happy path")
    class HappyPath {

        @Test
        @DisplayName("invia la risposta su mapping.response quando il handler ha successo")
        void sendsSuccessResponse() {
            MessageEnvelope<CreateMappingRequest> env = buildEnvelope();
            CreateMappingRequest req = env.getPayload();
            MappingResponse mappingResp = new MappingResponse(1L, "https://x.com", "code1", 1L);

            MappingRequestHandler handler = mock(MappingRequestHandler.class);
            // doReturn evita il problema di type inference con il wildcard di convertValue
            doReturn(req).when(mapper).convertValue(any(), any(Class.class));
            when(mappingRequestHandlerFactory.getHandler(any())).thenReturn(handler);
            when(handler.handleRequest(any())).thenReturn(List.of(mappingResp));

            consumerService.handleMessage(env);

            // Uso ArgumentCaptor per evitare ambiguità tra overload di convertAndSend
            ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
            verify(rabbitTemplate).convertAndSend(
                    eq("reply.shortener.exchange"),
                    eq("mapping.response"),
                    captor.capture());
            assertThat(captor.getValue()).isInstanceOf(MessageEnvelope.class);
        }

        @Test
        @DisplayName("la risposta di successo contiene il correlationId originale")
        void preservesCorrelationId() {
            MessageEnvelope<CreateMappingRequest> env = buildEnvelope();
            CreateMappingRequest req = env.getPayload();
            MappingRequestHandler handler = mock(MappingRequestHandler.class);

            doReturn(req).when(mapper).convertValue(any(), any(Class.class));
            when(mappingRequestHandlerFactory.getHandler(any())).thenReturn(handler);
            when(handler.handleRequest(any())).thenReturn(List.of());

            consumerService.handleMessage(env);

            ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
            verify(rabbitTemplate).convertAndSend(
                    eq("reply.shortener.exchange"),
                    eq("mapping.response"),
                    captor.capture());
            assertThat(((MessageEnvelope<?>) captor.getValue()).getCorrelationId()).isEqualTo("corr-123");
        }
    }

    // ---------------------------------------------------------------------------
    // Gestione eccezioni
    // ---------------------------------------------------------------------------

    @Nested
    @DisplayName("gestione eccezioni")
    class ErrorHandling {

        private void setupMapper(MessageEnvelope<?> env) {
            // doReturn evita il problema di type inference con ObjectMapper.convertValue(?, Class<?>)
            doReturn(env.getPayload()).when(mapper).convertValue(any(), any(Class.class));
        }

        @Test
        @DisplayName("MappingAlreadyExistException → routing mapping.error con MAPPING_ALREADY_EXISTS")
        void handlesMappingAlreadyExists() {
            MessageEnvelope<CreateMappingRequest> env = buildEnvelope();
            setupMapper(env);

            MappingRequestHandler handler = mock(MappingRequestHandler.class);
            when(mappingRequestHandlerFactory.getHandler(any())).thenReturn(handler);
            when(handler.handleRequest(any()))
                    .thenThrow(new MappingAlreadyExistException("dup"));

            consumerService.handleMessage(env);

            ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
            verify(rabbitTemplate).convertAndSend(
                    eq("reply.shortener.exchange"), eq("mapping.error"), captor.capture());

            ErrorResponse error = (ErrorResponse) ((MessageEnvelope<?>) captor.getValue()).getPayload();
            assertThat(error.error()).isEqualTo("MAPPING_ALREADY_EXISTS");
        }

        @Test
        @DisplayName("MappingNotFoundException → routing mapping.error con MAPPING_NOT_FOUND")
        void handlesMappingNotFound() {
            MessageEnvelope<CreateMappingRequest> env = buildEnvelope();
            setupMapper(env);

            MappingRequestHandler handler = mock(MappingRequestHandler.class);
            when(mappingRequestHandlerFactory.getHandler(any())).thenReturn(handler);
            when(handler.handleRequest(any()))
                    .thenThrow(new MappingNotFoundException("not found"));

            consumerService.handleMessage(env);

            ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
            verify(rabbitTemplate).convertAndSend(
                    eq("reply.shortener.exchange"), eq("mapping.error"), captor.capture());

            ErrorResponse error = (ErrorResponse) ((MessageEnvelope<?>) captor.getValue()).getPayload();
            assertThat(error.error()).isEqualTo("MAPPING_NOT_FOUND");
        }

        @Test
        @DisplayName("UnknownRequestPayloadType → routing auth.error")
        void handlesUnknownPayloadType() {
            MessageEnvelope<CreateMappingRequest> env = buildEnvelope();
            setupMapper(env);

            MappingRequestHandler handler = mock(MappingRequestHandler.class);
            when(mappingRequestHandlerFactory.getHandler(any())).thenReturn(handler);
            when(handler.handleRequest(any()))
                    .thenThrow(new UnknownRequestPayloadType("unknown"));

            consumerService.handleMessage(env);

            ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
            verify(rabbitTemplate).convertAndSend(
                    eq("reply.shortener.exchange"), eq("auth.error"), captor.capture());
            assertThat(captor.getValue()).isInstanceOf(MessageEnvelope.class);
        }

        @Test
        @DisplayName("IllegalArgumentException → routing auth.error")
        void handlesIllegalArgument() {
            MessageEnvelope<CreateMappingRequest> env = buildEnvelope();
            setupMapper(env);

            MappingRequestHandler handler = mock(MappingRequestHandler.class);
            when(mappingRequestHandlerFactory.getHandler(any())).thenReturn(handler);
            when(handler.handleRequest(any()))
                    .thenThrow(new IllegalArgumentException("bad arg"));

            consumerService.handleMessage(env);

            ArgumentCaptor<Object> captor2 = ArgumentCaptor.forClass(Object.class);
            verify(rabbitTemplate).convertAndSend(
                    eq("reply.shortener.exchange"), eq("auth.error"), captor2.capture());
            assertThat(captor2.getValue()).isInstanceOf(MessageEnvelope.class);
        }

        @Test
        @DisplayName("Eccezione generica → routing error.internal")
        void handlesGenericException() {
            MessageEnvelope<CreateMappingRequest> env = buildEnvelope();
            setupMapper(env);

            MappingRequestHandler handler = mock(MappingRequestHandler.class);
            when(mappingRequestHandlerFactory.getHandler(any())).thenReturn(handler);
            when(handler.handleRequest(any()))
                    .thenThrow(new RuntimeException("boom"));

            consumerService.handleMessage(env);

            ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
            verify(rabbitTemplate).convertAndSend(
                    eq("reply.shortener.exchange"), eq("error.internal"), captor.capture());
            assertThat(captor.getValue()).isInstanceOf(MessageEnvelope.class);
        }
    }
}