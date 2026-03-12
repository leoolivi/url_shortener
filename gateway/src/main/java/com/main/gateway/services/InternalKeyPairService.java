package com.main.gateway.services;

import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Service;

import com.urlshortener.security.KeyPairService;
import com.urlshortener.security.keys.StringKeyPair;
import com.urlshortener.utils.KeyPairGeneratorUtil;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class InternalKeyPairService implements KeyPairService{

    private final KeyPairGeneratorUtil generatorUtil;
    private final InternalKeyHolder internalKeyHolder;

    @Override
    @Transactional
    public StringKeyPair generateStringKeyPair(int size) throws NoSuchAlgorithmException {
        StringKeyPair skp = generatorUtil.generateStringKeyPair(size);
        internalKeyHolder.setPrivateKey(skp.getPrivateKey());
        internalKeyHolder.setPublicKey(skp.getPublicKey());
        return skp;
    }

    @Override
    public StringKeyPair generateStringKeyPair() throws NoSuchAlgorithmException {
        return generateStringKeyPair(2048);
    }

    @Override
    public String findCurrentPublicKey() {
        return internalKeyHolder.getPublicKey();
    }

    @Override
    public String findCurrentPrivateKey() {
        return internalKeyHolder.getPrivateKey();
    }
}
