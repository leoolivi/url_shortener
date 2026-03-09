package com.main.auth.services;

import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Service;

import com.main.auth.domain.models.UserKeyPair;
import com.main.auth.repositories.UserKeyPairRepository;
import com.urlshortener.security.KeyPairService;
import com.urlshortener.security.keys.StringKeyPair;
import com.urlshortener.security.keys.user.UserTokenPolicyProvider;
import com.urlshortener.utils.KeyPairGeneratorUtil;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserKeyPairService implements KeyPairService {

    private final KeyPairGeneratorUtil generatorUtil;
    private final UserKeyPairRepository keyPairRepository;
    private final UserTokenPolicyProvider tokenPolicyProvider;

    @Override
    @Transactional
    public StringKeyPair generateStringKeyPair(int size) throws NoSuchAlgorithmException {
        StringKeyPair skp = generatorUtil.generateStringKeyPair(size);
        var newAppKeyPair = UserKeyPair.builder()
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
        var userKeyPair = keyPairRepository.findCurrentUserKeyPair();
        return new StringKeyPair(userKeyPair.getPrivateKey(), userKeyPair.getPublicKey());
    }
}
