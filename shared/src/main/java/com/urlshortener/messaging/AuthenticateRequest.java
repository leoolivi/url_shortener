package com.urlshortener.messaging;

public record AuthenticateRequest(
    String email,
    String password
) {}
