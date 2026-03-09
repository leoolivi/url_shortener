package com.main.gateway.services;

import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Service;

import com.main.gateway.domain.models.InternalKeyPair;
import com.main.gateway.repositories.InternalKeyPairRepository;
import com.urlshortener.security.KeyPairService;
import com.urlshortener.security.keys.StringKeyPair;
import com.urlshortener.security.keys.internal.InternalTokenPolicyProvider;
import com.urlshortener.utils.KeyPairGeneratorUtil;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class InternalKeyPairService implements KeyPairService{

    private final KeyPairGeneratorUtil generatorUtil;
    private final InternalKeyPairRepository keyPairRepository;
    private final InternalTokenPolicyProvider tokenPolicyProvider;

    @Override
    @Transactional
    public StringKeyPair generateStringKeyPair(int size) throws NoSuchAlgorithmException {
        StringKeyPair skp = generatorUtil.generateStringKeyPair(size);
        var newAppKeyPair = InternalKeyPair.builder()
                                .privateKey(skp.getPrivateKey())
                                .publicKey(skp.getPublicKey())
                                .expiresAt(System.currentTimeMillis()+tokenPolicyProvider.getExpiration())
                                .build();
        keyPairRepository.invalidateAll();
        keyPairRepository.save(newAppKeyPair);
        return skp;
    }

    @Override
    public StringKeyPair generateStringKeyPair() throws NoSuchAlgorithmException {
        return generateStringKeyPair(2048);
    }

    @Override
    public StringKeyPair findCurrentStringKeyPair() {
        var internalKeyPair = keyPairRepository.findCurrentInternalKeyPair();
        return new StringKeyPair(internalKeyPair.getPrivateKey(), internalKeyPair.getPublicKey());
    }
}
