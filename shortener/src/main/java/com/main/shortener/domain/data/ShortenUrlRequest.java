package com.main.shortener.domain.data;

public record ShortenUrlRequest(
    String originalUrl,
    String code,
    Long userId
) {}
