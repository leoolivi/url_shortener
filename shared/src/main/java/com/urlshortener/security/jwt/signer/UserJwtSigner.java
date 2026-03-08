package com.urlshortener.security.jwt.signer;

import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.urlshortener.data.user.UserClaims;
import com.urlshortener.security.keys.user.UserKeyProvider;
import com.urlshortener.security.keys.user.UserTokenPolicyProvider;

import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserJwtSigner implements JwtSigner<UserClaims> {

    private final UserKeyProvider keyProvider;
    private final UserTokenPolicyProvider tokenPolicyProvider;

    @Override
    public String signToken(
            Map<String, UserClaims> extraClaims,
            UserClaims userDetails
    ) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .claim("payload", userDetails)
                .subject(userDetails.email())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenPolicyProvider.getExpiration()))
                .signWith(keyProvider.getPrivateKey())
                .compact();
    }

    @Override
    public String signToken(
        UserClaims userDetails
    ) {
        return signToken(null, userDetails);
    }
}
