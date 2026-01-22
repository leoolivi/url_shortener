package com.urlshortener.messaging;

public record ErrorResponse (
    String errorCode,
    String message
) {}
