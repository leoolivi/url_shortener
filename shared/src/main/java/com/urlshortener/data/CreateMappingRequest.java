package com.urlshortener.data;

public record CreateMappingRequest(
    String originalUrl,
    String code
) {}
