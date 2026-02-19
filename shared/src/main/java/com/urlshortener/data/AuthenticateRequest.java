package com.urlshortener.data;

public record AuthenticateRequest(
    String email,
    String password
) {}
