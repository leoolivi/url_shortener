package com.urlshortener.data.response.keys;

import lombok.Getter;

@Getter
public abstract class KeyResponse {
    private String privateKey;

    public KeyResponse(String privateKey) {
        this.privateKey = privateKey;
    }
}
