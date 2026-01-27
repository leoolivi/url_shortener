package com.urlshortener.messaging;

public record UpdateUserRequest(
    Long id,
    String email,
    String password,
    String role
) {}
