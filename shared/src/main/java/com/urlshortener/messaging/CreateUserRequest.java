package com.urlshortener.messaging;

public record CreateUserRequest (
    String email,
    String password,
    String role
) {}