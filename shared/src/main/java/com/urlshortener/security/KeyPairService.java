package com.urlshortener.security;

import java.security.NoSuchAlgorithmException;

import com.urlshortener.security.keys.StringKeyPair;

public interface KeyPairService {
    public StringKeyPair generateStringKeyPair() throws NoSuchAlgorithmException;
    public StringKeyPair generateStringKeyPair(int size) throws NoSuchAlgorithmException;
    public StringKeyPair findCurrentStringKeyPair();
}
