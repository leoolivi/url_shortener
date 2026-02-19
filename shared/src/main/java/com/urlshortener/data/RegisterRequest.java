package com.urlshortener.data;

public record RegisterRequest(
    String email,
    String password,
    String role
) {}
