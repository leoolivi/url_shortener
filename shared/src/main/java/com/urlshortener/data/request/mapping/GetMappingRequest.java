package com.urlshortener.data.request.mapping;

public record GetMappingRequest (
    String code
) implements MappingRequest {}
