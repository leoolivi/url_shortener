package com.urlshortener.security.keys.internal;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.urlshortener.security.KeyPairService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class InternalHttpKeyProvider implements InternalKeyProvider {

    private final KeyPairService keyPairService;

    @Override
    public Optional<PrivateKey> getPrivateKey() {
        var privKeyStr = keyPairService.findCurrentPrivateKey();

        byte[] encoded = Base64.getDecoder().decode(privKeyStr);
        
        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            return Optional.of(keyFactory.generatePrivate(keySpec));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return Optional.empty();
        }
        
    }

    @Override
    public Optional<PublicKey> getPublicKey() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPublicKey'");
    }
    
}
