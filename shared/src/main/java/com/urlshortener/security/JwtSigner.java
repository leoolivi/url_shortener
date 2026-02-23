package com.urlshortener.security;

import java.io.File;
import java.security.interfaces.RSAPrivateKey;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.urlshortener.data.response.user.UserResponse;
import com.urlshortener.utils.PemUtil;

import io.jsonwebtoken.Jwts;

@Service
public class JwtSigner {
    
    @Value("${security.jwt.private-key-path}")
    private String privateKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    public String signToken(
            Map<String, Object> extraClaims,
            UserResponse userDetails
    ) throws Exception {
        return Jwts
                .builder()
                .claims(extraClaims)
                .claim("id", userDetails.getId())
                .claim("role", userDetails.getRole())
                .claim("details", new UserResponse(userDetails.getId(), userDetails.getEmail(), userDetails.getPassword(), userDetails.getRole()))
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String signToken(
        UserResponse userDetails
    ) throws Exception {
        return signToken(null, userDetails);
    }

    private RSAPrivateKey getSigningKey() throws Exception {
        File pem = new File(privateKey);
        return PemUtil.readPrivateKey(pem);
    }

}
