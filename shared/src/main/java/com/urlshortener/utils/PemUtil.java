package com.urlshortener.utils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class PemUtil {

    public static RSAPublicKey readPublicKey(File file) throws Exception {
        String key = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        
        // Remove PEM headers/footers and all whitespace
        String publicKeyPEM = key
            .replaceAll("-----BEGIN (.*)-----", "")
            .replaceAll("-----END (.*)-----", "")
            .replaceAll("\\s", "");
        
        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
        
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    public static RSAPrivateKey readPrivateKey(File file) throws Exception {
        String key = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        
        // Remove PEM headers/footers and all whitespace
        String privateKeyPEM = key
            .replaceAll("-----BEGIN (.*)-----", "")
            .replaceAll("-----END (.*)-----", "")
            .replaceAll("\\s", "");
        
        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
        
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        
        // Try PKCS#8 format first
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            // If PKCS#8 fails, the key might be in PKCS#1 format
            // You need to convert PKCS#1 to PKCS#8
            throw new Exception("Private key must be in PKCS#8 format. " +
                "Convert with: openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt " +
                "-in private.pem -out private_pkcs8.pem", e);
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println(readPrivateKey(new File("/Users/olivi/Code/url_shortener/auth/src/main/resources/jwt-private-pkcs8.pem")).getClass());
    }
}