package com.main.gateway.domain.data;

public record ShortenUrlRequest (
    String originalUrl,
    String code,
    Long userId
) {}
