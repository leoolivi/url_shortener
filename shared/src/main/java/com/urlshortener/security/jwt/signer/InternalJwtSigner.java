package com.urlshortener.security.jwt.signer;

import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.urlshortener.data.internal.InternalClaims;
import com.urlshortener.security.keys.internal.InternalKeyProvider;
import com.urlshortener.security.keys.internal.InternalTokenPolicyProvider;

import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class InternalJwtSigner implements JwtSigner<InternalClaims> {

    private final InternalKeyProvider keyProvider;
    private final InternalTokenPolicyProvider tokenPolicyProvider;

    @Override
    public String signToken(
            Map<String, InternalClaims> extraClaims,
            InternalClaims claims
    ) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(claims.serviceName())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenPolicyProvider.getExpiration()))
                .signWith(keyProvider.getPrivateKey().get())
                .compact();
    }

    @Override
    public String signToken(
        InternalClaims claims
    ) {
        return signToken(null, claims);
    }
}

