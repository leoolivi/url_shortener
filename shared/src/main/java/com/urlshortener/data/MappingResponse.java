package com.urlshortener.data;

public record MappingResponse(
    Long id,
    String originalUrl,
    String code,
    Long userId
) implements Response {}
