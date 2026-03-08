package com.urlshortener.security.keys.user;

import com.urlshortener.security.JwtProperties;
import com.urlshortener.security.keys.TokenPolicyProvider;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserTokenPolicyProvider implements TokenPolicyProvider {

    private final JwtProperties jwtProps;

    @Override
    public long getExpiration() {
        return jwtProps.getUser().getExpiration();
    }
    
}
