package com.urlshortener.messaging;

public record RedirectResponse(
    String code,
    String originalUrl
) implements Response {}
