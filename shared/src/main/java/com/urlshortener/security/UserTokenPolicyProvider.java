package com.urlshortener.security;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserTokenPolicyProvider implements TokenPolicyProvider {

    private final JwtProperties jwtProps;

    @Override
    public long getExpiration() {
        return jwtProps.getUser().getExpiration();
    }
    
}
