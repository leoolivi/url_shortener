package com.urlshortener.data.user;

import com.urlshortener.data.JwtClaims;

public record UserClaims(Long id, String email, String role, Long exp) implements JwtClaims {}
