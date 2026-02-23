package com.urlshortener.data.request.auth;

import com.urlshortener.data.Request;

public record AuthenticateRequest(
    String email,
    String password
) implements Request {}
