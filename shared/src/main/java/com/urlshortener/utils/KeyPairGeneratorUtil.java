package com.urlshortener.utils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import com.urlshortener.security.keys.StringKeyPair;

import jakarta.annotation.PostConstruct;


public class KeyPairGeneratorUtil {

    private KeyPairGenerator generator;

    @PostConstruct
    public void setup() throws NoSuchAlgorithmException {
        this.generator = KeyPairGenerator.getInstance("RSA");
    }

    public StringKeyPair generateStringKeyPair(int size) throws NoSuchAlgorithmException {
        generator.initialize(2048);
        KeyPair kp = generator.generateKeyPair();
        String privateKey = Base64.getEncoder().encodeToString(kp.getPrivate().getEncoded());
        String publicKey = Base64.getEncoder().encodeToString(kp.getPublic().getEncoded());
        return new StringKeyPair(privateKey, publicKey);
    }

    public StringKeyPair generateStringKeyPair() throws NoSuchAlgorithmException {
        return generateStringKeyPair(2048);
    }
    
    public static void main(String[] args) throws NoSuchAlgorithmException {
        var generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair kp = generator.generateKeyPair();
        String privateKey = Base64.getEncoder().encodeToString(kp.getPrivate().getEncoded());
        System.out.println("Private key: "+privateKey);

        String publicKey = Base64.getEncoder().encodeToString(kp.getPublic().getEncoded());
        System.out.println("Public key: "+publicKey);
    }
}
