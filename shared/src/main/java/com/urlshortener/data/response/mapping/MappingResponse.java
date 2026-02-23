package com.urlshortener.data.response.mapping;

import com.urlshortener.data.Response;

public record MappingResponse(
    Long id,
    String originalUrl,
    String code,
    Long userId
) implements Response {}
