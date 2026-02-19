package com.urlshortener.data;

public record UpdateMappingRequest (
    Long id,
    String originalUrl,
    String code,
    Long userId
) {}
