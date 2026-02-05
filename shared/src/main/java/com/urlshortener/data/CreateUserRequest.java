package com.urlshortener.data;

public record CreateUserRequest (
    String email,
    String password,
    String role
) {}