package com.urlshortener.data.request.mapping;

public record UpdateMappingRequest (
    Long id,
    String originalUrl,
    String code,
    Long userId
) implements MappingRequest {}
