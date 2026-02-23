package com.urlshortener.data.request.mapping;

public record DeleteMappingRequest (
    String code
) implements MappingRequest {}
