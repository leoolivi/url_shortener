package com.urlshortener.messaging;

public record MappingResponse(
    Long id,
    String originalUrl,
    String code
) {}
