package com.urlshortener.security.keys;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StringKeyPair {
    private String privateKey;
    private String publicKey;
}
