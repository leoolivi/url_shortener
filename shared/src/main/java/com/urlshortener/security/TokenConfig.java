package com.urlshortener.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenConfig {
    private String privateKey;
    private String publicKey;
    private long expiration;    
}
