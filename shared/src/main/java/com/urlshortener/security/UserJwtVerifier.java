package com.urlshortener.security;

import java.util.Date;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urlshortener.data.user.UserClaims;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserJwtVerifier implements JwtVerifier<UserClaims> {

    private final UserKeyProvider keyProvider;
    private final ObjectMapper objectMapper;

    public String extractUsername(String token) throws JwtException, IllegalArgumentException, Exception {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws JwtException, IllegalArgumentException, Exception {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    public UserClaims extractPayload(String token) throws IllegalArgumentException, Exception {
        if (token == null) {
            throw new IllegalArgumentException("Token must not be null");
        }
        Claims claims = extractAllClaims(token);
        var rawPayload = claims.get("payload");
        UserClaims details = objectMapper.convertValue(rawPayload, UserClaims.class);
        
        if (details == null) {
            throw new IllegalArgumentException("User details not found in token");
        }
        
        return details;
    }

    @Override
    public boolean isTokenValid(String token, UserClaims userClaims) throws JwtException, IllegalArgumentException, Exception {
        final String username = extractUsername(token);
        return (username.equals(userClaims.email())) && !isTokenExpired(token);
    }

    @Override
    public boolean isTokenExpired(String token) throws JwtException, IllegalArgumentException, Exception {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) throws JwtException, IllegalArgumentException, Exception {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) throws JwtException, IllegalArgumentException, Exception {
        return Jwts
                .parser()
                .verifyWith(keyProvider.getPublicKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
