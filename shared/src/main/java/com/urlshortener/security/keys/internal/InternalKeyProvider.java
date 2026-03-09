package com.urlshortener.security.keys.internal;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Optional;

// KeyProvider.java
public interface InternalKeyProvider {
    Optional<PrivateKey> getPrivateKey();
    Optional<PublicKey> getPublicKey();
}