package com.urlshortener.security;
public interface TokenPolicyProvider {
    public long getExpiration();
}
