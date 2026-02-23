package com.urlshortener.data.request.user;

public record CreateUserRequest (
    String email,
    String password,
    String role
) {}