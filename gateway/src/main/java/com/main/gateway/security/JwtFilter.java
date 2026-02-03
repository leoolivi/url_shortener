package com.main.gateway.security;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import com.main.gateway.domain.data.PrincipalUser;
import com.main.gateway.services.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

// @Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            var principal = new PrincipalUser(jwtService.extractUsername(token), jwtService.extractClaim(token, "role", String.class));
            
        }
    }
    
}
