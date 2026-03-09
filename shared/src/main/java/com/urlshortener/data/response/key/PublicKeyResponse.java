package com.urlshortener.data.response.key;

import com.urlshortener.data.Response;

public record PublicKeyResponse (
    String publicKey
) implements Response {}
