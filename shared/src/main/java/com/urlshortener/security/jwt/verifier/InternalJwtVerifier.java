package com.urlshortener.security.jwt.verifier;

import java.util.Date;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.urlshortener.data.internal.InternalClaims;
import com.urlshortener.security.keys.internal.InternalKeyProvider;
import com.urlshortener.security.keys.internal.InternalTokenPolicyProvider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class InternalJwtVerifier implements JwtVerifier<InternalClaims> {

    private final InternalKeyProvider keyProvider;
    private final InternalTokenPolicyProvider tokenPolicyProvider;

    public String extractSubject(String token) throws JwtException, IllegalArgumentException, Exception {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws JwtException, IllegalArgumentException, Exception {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) throws JwtException, IllegalArgumentException, Exception {
        return Jwts
                .parser()
                .verifyWith(keyProvider.getPublicKey().get())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public boolean isTokenValid(String token, InternalClaims claims)
            throws JwtException, IllegalArgumentException, Exception {
        final String username = extractSubject(token);
        return (username.equals(claims.serviceName())) && !isTokenExpired(token);
    }

    @Override
    public boolean isTokenExpired(String token) throws JwtException, IllegalArgumentException, Exception {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) throws JwtException, IllegalArgumentException, Exception {
        return extractClaim(token, Claims::getExpiration);
    }
    
}
