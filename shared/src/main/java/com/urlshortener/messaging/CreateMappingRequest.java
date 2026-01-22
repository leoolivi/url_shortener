package com.urlshortener.messaging;

public record CreateMappingRequest(
    String originalUrl,
    String code,
    Long userId
) {}
