package com.urlshortener.security.keys.internal;

import com.urlshortener.security.JwtProperties;
import com.urlshortener.security.keys.TokenPolicyProvider;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InternalTokenPolicyProvider implements TokenPolicyProvider {

    private final JwtProperties jwtProps;

    @Override
    public long getExpiration() {
        return jwtProps.getInternal().getExpiration();
    }
    
}
