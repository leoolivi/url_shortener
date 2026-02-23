package com.urlshortener.data.request.auth;

import com.urlshortener.data.Request;

public record RegisterRequest(
    String email,
    String password,
    String role
) implements Request {}
