package com.urlshortener.security.keys.user;

import java.security.PrivateKey;
import java.security.PublicKey;

// KeyProvider.java
public interface UserKeyProvider {
    PrivateKey getPrivateKey();
    PublicKey getPublicKey();
}