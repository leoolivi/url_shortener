package com.urlshortener.data;

public record ErrorResponse (
    String error,
    String message
) {
}
