package com.main.auth.services;

import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Service;

import com.urlshortener.security.KeyPairService;
import com.urlshortener.security.keys.StringKeyHolder;
import com.urlshortener.security.keys.StringKeyPair;
import com.urlshortener.utils.KeyPairGeneratorUtil;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserKeyPairService implements KeyPairService {

    private final KeyPairGeneratorUtil generatorUtil;
    private final StringKeyHolder userKeyHolder;
    
    @Override
    @Transactional
    public StringKeyPair generateStringKeyPair(int size) throws NoSuchAlgorithmException {
        StringKeyPair skp = generatorUtil.generateStringKeyPair(size);
        userKeyHolder.setPrivateKey(skp.getPrivateKey());
        userKeyHolder.setPublicKey(skp.getPublicKey());
        return skp;
    }

    @Override
    public StringKeyPair generateStringKeyPair() throws NoSuchAlgorithmException {
        return generateStringKeyPair(2048);
    }

    @Override
    public String findCurrentPublicKey() {
        return userKeyHolder.getPublicKey();
    }

    @Override
    public String findCurrentPrivateKey() {
        return userKeyHolder.getPrivateKey();
    }
}
