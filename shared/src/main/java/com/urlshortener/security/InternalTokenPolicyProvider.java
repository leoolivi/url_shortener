package com.urlshortener.security;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InternalTokenPolicyProvider implements TokenPolicyProvider {

    private final JwtProperties jwtProps;

    @Override
    public long getExpiration() {
        return jwtProps.getInternal().getExpiration();
    }
    
}
