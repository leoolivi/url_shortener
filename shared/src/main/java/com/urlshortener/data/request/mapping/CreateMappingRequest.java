package com.urlshortener.data.request.mapping;

public record CreateMappingRequest(
    String originalUrl,
    String code,
    Long userId
) implements MappingRequest {}
