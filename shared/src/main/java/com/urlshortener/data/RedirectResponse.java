package com.urlshortener.data;

public record RedirectResponse(
    String code,
    String originalUrl
) implements Response {}
