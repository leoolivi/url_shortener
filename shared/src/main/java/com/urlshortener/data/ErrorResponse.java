package com.urlshortener.data;

public record ErrorResponse (
    String errorCode,
    String message
) {}
