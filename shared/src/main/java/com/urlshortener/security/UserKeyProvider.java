package com.urlshortener.security;

import java.security.PrivateKey;
import java.security.PublicKey;

// KeyProvider.java
public interface UserKeyProvider {
    PrivateKey getPrivateKey();
    PublicKey getPublicKey();
}