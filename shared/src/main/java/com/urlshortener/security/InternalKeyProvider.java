package com.urlshortener.security;

import java.security.PrivateKey;
import java.security.PublicKey;

// KeyProvider.java
public interface InternalKeyProvider {
    PrivateKey getPrivateKey();
    PublicKey getPublicKey();
}