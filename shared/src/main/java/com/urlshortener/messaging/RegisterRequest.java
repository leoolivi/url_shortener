package com.urlshortener.messaging;

public record RegisterRequest(
    String email,
    String password,
    String role
) {}
