package com.urlshortener.data.request.user;

public record UpdateUserRequest(
    Long id,
    String email,
    String password,
    String role
) {}
