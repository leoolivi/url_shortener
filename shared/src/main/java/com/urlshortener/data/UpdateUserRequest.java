package com.urlshortener.data;

public record UpdateUserRequest(
    Long id,
    String email,
    String password,
    String role
) {}
