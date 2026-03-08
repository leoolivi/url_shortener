package com.urlshortener.data.user;

public record AuthenticatedUser(Long id, String email, String role) {}
