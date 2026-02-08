package com.main.gateway.security;

import java.io.IOException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.main.gateway.services.JwtService;
import com.urlshortener.data.User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("filter loaded");
        if (request.getServletPath().startsWith("/api/v1/auth")) {
            log.info("Filter bypassed (auth endpoints)");
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                User user = jwtService.extractUser(token);
                log.info("User loaded: {}", user);
                var email = jwtService.extractUsername(token);
                if (email != null && jwtService.isTokenValid(token, user)) {
                    JwtAuthenticationToken auth = new JwtAuthenticationToken(user, token, user.getAuthorities());
                    auth.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception e) {
                log.warn("JWT token parsing failed: {}", e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }
    
}
