package com.urlshortener.security.jwt.verifier;

import com.urlshortener.data.JwtClaims;

import io.jsonwebtoken.JwtException;

public interface JwtVerifier<T extends JwtClaims> {
    public boolean isTokenValid(String token, T claims) throws JwtException, IllegalArgumentException, Exception;
    public boolean isTokenExpired(String token) throws JwtException, IllegalArgumentException, Exception;
}

