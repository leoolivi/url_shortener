package com.urlshortener.messaging;

public record UpdateMappingRequest (
    Long id,
    String originalUrl,
    String code,
    Long userId
) {}
