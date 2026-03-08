package com.urlshortener.data.internal;

import com.urlshortener.data.JwtClaims;

public record InternalClaims(
    String serviceName
) implements JwtClaims {}
