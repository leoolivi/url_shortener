package com.urlshortener.security;

import java.io.File;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.urlshortener.data.response.user.UserResponse;
import com.urlshortener.utils.PemUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

@Service
public class JwtVerifier {
    
    @Value("${security.jwt.public-key-path}")
    private String publicKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;


    public String extractUsername(String token) throws JwtException, IllegalArgumentException, Exception {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws JwtException, IllegalArgumentException, Exception {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public long getExpirationTime() {
        return jwtExpiration;
    }
    
    public UserResponse extractUser(String token) throws IllegalArgumentException, Exception {
        if (token == null) {
            throw new IllegalArgumentException("Token must not be null");
        }
        Claims claims = extractAllClaims(token);
        Map<String, Object> detailsMap = claims.get("details", Map.class);
        
        if (detailsMap == null) {
            throw new IllegalArgumentException("User details not found in token");
        }
        
        Long id = ((Number) detailsMap.get("id")).longValue();
        String email = (String) detailsMap.get("email");
        String password = (String) detailsMap.get("password");
        String role = (String) detailsMap.get("role");
        
        return new UserResponse(id, email, password, role);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) throws JwtException, IllegalArgumentException, Exception {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) throws JwtException, IllegalArgumentException, Exception {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) throws JwtException, IllegalArgumentException, Exception {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) throws JwtException, IllegalArgumentException, Exception {
        return Jwts
                .parser()
                .verifyWith(getVerifyKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private RSAPublicKey getVerifyKey() throws Exception {
        File pem = new File(publicKey);
        return PemUtil.readPublicKey(pem);
    }

}

