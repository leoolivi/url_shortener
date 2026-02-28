package com.main.shortener.services;

import com.main.shortener.domain.models.UrlMapping;
import com.main.shortener.exceptions.UnsupportedRequestTypeException;
import com.main.shortener.services.factory.MappingRequestHandlerFactory;
import com.main.shortener.services.handlers.CreateMappingRequestHandler;
import com.main.shortener.services.handlers.DeleteMappingRequestHandler;
import com.main.shortener.services.handlers.GetAllMappingsRequestHandler;
import com.main.shortener.services.handlers.GetMappingRequestHandler;
import com.main.shortener.services.handlers.MappingRequestHandler;
import com.main.shortener.services.handlers.UpdateMappingRequestHandler;
import com.urlshortener.data.request.mapping.*;
import com.urlshortener.data.response.mapping.MappingResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test suite per i {@link MappingRequestHandler} del modulo shortener e
 * per la {@link MappingRequestHandlerFactory}.
 *
 * Ogni handler viene testato in isolamento usando mock di
 * {@link UrlMappingService} e {@link ObjectMapper}.
 *
 * Copre:
 *  - CreateMappingRequestHandler
 *  - DeleteMappingRequestHandler
 *  - UpdateMappingRequestHandler
 *  - GetMappingRequestHandler
 *  - GetAllMappingsRequestHandler
 *  - MappingRequestHandlerFactory  (routing corretto + tipo sconosciuto)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Shortener – MappingRequestHandlers")
class MappingRequestHandlerTest {

    @Mock
    private UrlMappingService urlMappingService;

    @Mock
    private ObjectMapper objectMapper;

    // ---------------------------------------------------------------------------
    // CreateMappingRequestHandler
    // ---------------------------------------------------------------------------

    @Nested
    @DisplayName("CreateMappingRequestHandler")
    class CreateHandler {

        private CreateMappingRequestHandler handler;

        @BeforeEach
        void setUp() {
            handler = new CreateMappingRequestHandler(urlMappingService, objectMapper);
        }

        @Test
        @DisplayName("getRequestClass() restituisce CreateMappingRequest.class")
        void returnsCorrectClass() {
            assertThat(handler.getRequestClass()).isEqualTo(CreateMappingRequest.class);
        }

        @Test
        @DisplayName("handleRequest() chiama createMapping e mappa la risposta")
        void delegatesToService() {
            CreateMappingRequest req = new CreateMappingRequest("https://x.com", "code1", 1L);
            UrlMapping saved = new UrlMapping(1L, "code1", "https://x.com", 1L);
            MappingResponse expectedResponse = new MappingResponse(1L, "https://x.com", "code1", 1L);

            when(objectMapper.convertValue(any(), eq(CreateMappingRequest.class))).thenReturn(req);
            when(urlMappingService.createMapping(req)).thenReturn(saved);
            when(objectMapper.convertValue(saved, MappingResponse.class)).thenReturn(expectedResponse);

            List<MappingResponse> result = handler.handleRequest(req);

            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(expectedResponse);
        }
    }

    // ---------------------------------------------------------------------------
    // DeleteMappingRequestHandler
    // ---------------------------------------------------------------------------

    @Nested
    @DisplayName("DeleteMappingRequestHandler")
    class DeleteHandler {

        private DeleteMappingRequestHandler handler;

        @BeforeEach
        void setUp() {
            handler = new DeleteMappingRequestHandler(urlMappingService, objectMapper);
        }

        @Test
        @DisplayName("getRequestClass() restituisce DeleteMappingRequest.class")
        void returnsCorrectClass() {
            assertThat(handler.getRequestClass()).isEqualTo(DeleteMappingRequest.class);
        }

        @Test
        @DisplayName("handleRequest() chiama deleteMappingByCode con il codice corretto")
        void delegatesToService() {
            DeleteMappingRequest req = new DeleteMappingRequest("code1");
            UrlMapping deleted = new UrlMapping(1L, "code1", "https://x.com", 1L);
            MappingResponse expectedResponse = new MappingResponse(1L, "https://x.com", "code1", 1L);

            when(objectMapper.convertValue(any(), eq(DeleteMappingRequest.class))).thenReturn(req);
            when(urlMappingService.deleteMappingByCode("code1")).thenReturn(deleted);
            when(objectMapper.convertValue(deleted, MappingResponse.class)).thenReturn(expectedResponse);

            List<MappingResponse> result = handler.handleRequest(req);

            assertThat(result).hasSize(1);
            verify(urlMappingService).deleteMappingByCode("code1");
        }
    }

    // ---------------------------------------------------------------------------
    // UpdateMappingRequestHandler
    // ---------------------------------------------------------------------------

    @Nested
    @DisplayName("UpdateMappingRequestHandler")
    class UpdateHandler {

        private UpdateMappingRequestHandler handler;

        @BeforeEach
        void setUp() {
            handler = new UpdateMappingRequestHandler(urlMappingService, objectMapper);
        }

        @Test
        @DisplayName("getRequestClass() restituisce UpdateMappingRequest.class")
        void returnsCorrectClass() {
            assertThat(handler.getRequestClass()).isEqualTo(UpdateMappingRequest.class);
        }

        @Test
        @DisplayName("handleRequest() chiama updateMapping con la request convertita")
        void delegatesToService() {
            UpdateMappingRequest req = new UpdateMappingRequest(1L, "https://new.com", "newcode", 1L);
            UrlMapping updated = new UrlMapping(1L, "newcode", "https://new.com", 1L);
            MappingResponse expectedResponse = new MappingResponse(1L, "https://new.com", "newcode", 1L);

            when(objectMapper.convertValue(any(), eq(UpdateMappingRequest.class))).thenReturn(req);
            when(urlMappingService.updateMapping(req)).thenReturn(updated);
            when(objectMapper.convertValue(updated, MappingResponse.class)).thenReturn(expectedResponse);

            List<MappingResponse> result = handler.handleRequest(req);

            assertThat(result).hasSize(1);
            verify(urlMappingService).updateMapping(req);
        }
    }

    // ---------------------------------------------------------------------------
    // GetMappingRequestHandler
    // ---------------------------------------------------------------------------

    @Nested
    @DisplayName("GetMappingRequestHandler")
    class GetHandler {

        private GetMappingRequestHandler handler;

        @BeforeEach
        void setUp() {
            handler = new GetMappingRequestHandler(urlMappingService, objectMapper);
        }

        @Test
        @DisplayName("getRequestClass() restituisce GetMappingRequest.class")
        void returnsCorrectClass() {
            assertThat(handler.getRequestClass()).isEqualTo(GetMappingRequest.class);
        }

        @Test
        @DisplayName("handleRequest() recupera il mapping per codice")
        void delegatesToService() {
            GetMappingRequest req = new GetMappingRequest("code1");
            UrlMapping found = new UrlMapping(1L, "code1", "https://x.com", 1L);
            MappingResponse expectedResponse = new MappingResponse(1L, "https://x.com", "code1", 1L);

            when(objectMapper.convertValue(any(), eq(GetMappingRequest.class))).thenReturn(req);
            when(urlMappingService.findByCode("code1")).thenReturn(found);
            when(objectMapper.convertValue(found, MappingResponse.class)).thenReturn(expectedResponse);

            List<MappingResponse> result = handler.handleRequest(req);

            assertThat(result).hasSize(1).containsExactly(expectedResponse);
        }
    }

    // ---------------------------------------------------------------------------
    // GetAllMappingsRequestHandler
    // ---------------------------------------------------------------------------

    @Nested
    @DisplayName("GetAllMappingsRequestHandler")
    class GetAllHandler {

        private GetAllMappingsRequestHandler handler;

        @BeforeEach
        void setUp() {
            handler = new GetAllMappingsRequestHandler(urlMappingService, objectMapper);
        }

        @Test
        @DisplayName("getRequestClass() restituisce GetAllMappingsRequest.class")
        void returnsCorrectClass() {
            assertThat(handler.getRequestClass()).isEqualTo(GetAllMappingsRequest.class);
        }

        @Test
        @DisplayName("handleRequest() restituisce tutti i mapping mappati in MappingResponse")
        void returnsAllMappings() {
            GetAllMappingsRequest req = new GetAllMappingsRequest();
            UrlMapping m1 = new UrlMapping(1L, "a", "https://a.com", 1L);
            UrlMapping m2 = new UrlMapping(2L, "b", "https://b.com", 2L);
            MappingResponse r1 = new MappingResponse(1L, "https://a.com", "a", 1L);
            MappingResponse r2 = new MappingResponse(2L, "https://b.com", "b", 2L);

            when(urlMappingService.getMappings()).thenReturn(List.of(m1, m2));
            when(objectMapper.convertValue(m1, MappingResponse.class)).thenReturn(r1);
            when(objectMapper.convertValue(m2, MappingResponse.class)).thenReturn(r2);

            List<MappingResponse> result = handler.handleRequest(req);

            assertThat(result).containsExactlyInAnyOrder(r1, r2);
        }

        @Test
        @DisplayName("handleRequest() restituisce lista vuota se non ci sono mapping")
        void returnsEmpty_whenNoMappings() {
            when(urlMappingService.getMappings()).thenReturn(List.of());

            List<MappingResponse> result = handler.handleRequest(new GetAllMappingsRequest());

            assertThat(result).isEmpty();
        }
    }

    // ---------------------------------------------------------------------------
    // MappingRequestHandlerFactory
    // ---------------------------------------------------------------------------

    @Nested
    @DisplayName("MappingRequestHandlerFactory")
    class Factory {

        private MappingRequestHandlerFactory factory;

        @BeforeEach
        void setUp() {
            factory = new MappingRequestHandlerFactory(urlMappingService, objectMapper);
        }

        @Test
        @DisplayName("restituisce CreateMappingRequestHandler per CreateMappingRequest.class")
        void returnsCreateHandler() {
            assertThat(factory.getHandler(CreateMappingRequest.class))
                    .isInstanceOf(CreateMappingRequestHandler.class);
        }

        @Test
        @DisplayName("restituisce UpdateMappingRequestHandler per UpdateMappingRequest.class")
        void returnsUpdateHandler() {
            assertThat(factory.getHandler(UpdateMappingRequest.class))
                    .isInstanceOf(UpdateMappingRequestHandler.class);
        }

        @Test
        @DisplayName("restituisce DeleteMappingRequestHandler per DeleteMappingRequest.class")
        void returnsDeleteHandler() {
            assertThat(factory.getHandler(DeleteMappingRequest.class))
                    .isInstanceOf(DeleteMappingRequestHandler.class);
        }

        @Test
        @DisplayName("restituisce GetAllMappingsRequestHandler per GetAllMappingsRequest.class")
        void returnsGetAllHandler() {
            assertThat(factory.getHandler(GetAllMappingsRequest.class))
                    .isInstanceOf(GetAllMappingsRequestHandler.class);
        }

        @Test
        @DisplayName("restituisce GetMappingRequestHandler per GetMappingRequest.class")
        void returnsGetHandler() {
            assertThat(factory.getHandler(GetMappingRequest.class))
                    .isInstanceOf(GetMappingRequestHandler.class);
        }

        @Test
        @DisplayName("lancia UnsupportedRequestTypeException per tipo sconosciuto")
        void throwsException_forUnknownType() {
            assertThatThrownBy(() -> factory.getHandler(String.class))
                    .isInstanceOf(UnsupportedRequestTypeException.class);
        }
    }
}
