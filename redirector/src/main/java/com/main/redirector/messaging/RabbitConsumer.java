package com.main.redirector.messaging;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;

import com.main.redirector.domain.data.CreateMappingRequest;
import com.main.redirector.domain.models.UrlMapping;
import com.main.redirector.services.UrlMappingService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
@AllArgsConstructor
public class RabbitConsumer {

    private final UrlMappingService service;

    public void consumeMessage(byte[] message) {
        String payload = new String(message, StandardCharsets.UTF_8);
        log.info("Consumed message:  {}", payload);
        var objectMapper = new ObjectMapper();
        var mapping = objectMapper.readValue(message, UrlMapping.class);
        service.createMapping(new CreateMappingRequest(mapping.getCode(), mapping.getOriginalUrl()));
    }
}
