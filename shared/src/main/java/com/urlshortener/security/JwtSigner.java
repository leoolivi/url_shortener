package com.urlshortener.security;

import java.util.Map;

import com.urlshortener.data.JwtClaims;

public interface JwtSigner<T extends JwtClaims> {
    public String signToken(Map<String, T> extraClaims, T claims);
    public String signToken(T claims);
}
