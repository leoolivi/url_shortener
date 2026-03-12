package com.urlshortener.security.keys;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.Setter;

@Service
@Setter
public abstract class StringKeyHolder {
    private volatile String privateKey;
    private volatile String publicKey;

    @PostConstruct
    public void setup() {
        this.privateKey = "";
        this.publicKey = "";
    }

    public String getPrivateKey() {
        if (privateKey == null) throw new IllegalStateException("Private key not yet loaded");
        return privateKey;
    }

    public String getPublicKey() {
        if (publicKey == null) throw new IllegalStateException("Public key not yet loaded");
        return publicKey;
    }

    public StringKeyPair getKeyPair() {
        return new StringKeyPair(privateKey, publicKey);
    }
}
