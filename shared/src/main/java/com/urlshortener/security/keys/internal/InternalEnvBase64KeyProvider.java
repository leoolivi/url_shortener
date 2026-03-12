package com.urlshortener.security.keys.internal;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.urlshortener.security.JwtProperties;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

// impl/EnvBase64KeyProvider.java
@Component
@ConditionalOnProperty(prefix = "security.jwt", name = "internal", matchIfMissing = true)
@RequiredArgsConstructor
public class InternalEnvBase64KeyProvider implements InternalKeyProvider {

    private final JwtProperties jwtProps;

    // Cache: le chiavi vengono decodificate una volta sola
    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct
    public void init() {
        String privateKeyBase64 = jwtProps.getInternal().getPrivateKey();
        String publicKeyBase64 = jwtProps.getInternal().getPublicKey();

        // Decodifica base64 → bytes → chiave RSA
        if (privateKeyBase64 != null && !privateKeyBase64.isBlank()) {
            this.privateKey = parsePrivateKey(privateKeyBase64);
        }
        this.publicKey = parsePublicKey(publicKeyBase64);
    }

    @Override
    public Optional<PublicKey> getPublicKey() {
        return Optional.of(publicKey);
    }

    @Override
    public Optional<PrivateKey> getPrivateKey() {
        return Optional.of(privateKey);
    }

    private PrivateKey parsePrivateKey(String base64) {
        try {
            byte[] decoded = Base64.getDecoder().decode(base64);
            // Rimuove header/footer PEM se presenti, poi decodifica
            String pem = new String(decoded)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(pem);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse private key", e);
        }
    }

    private PublicKey parsePublicKey(String base64) {
        try {
            byte[] decoded = Base64.getDecoder().decode(base64);
            String pem = new String(decoded)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(pem);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse public key", e);
        }
    }


}
