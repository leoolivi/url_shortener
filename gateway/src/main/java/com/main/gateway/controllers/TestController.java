package com.main.gateway.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.gateway.security.JwtAuthenticationToken;


@RestController
@RequestMapping("/api/v1/test")
public class TestController {
    @GetMapping("test")
    public String test(@PathVariable String param) {
        return "Test parametro: " + param;
    }

    @GetMapping("me")
    public ResponseEntity<?> me(@AuthenticationPrincipal JwtAuthenticationToken authentication) {
        return ResponseEntity.ok(authentication);
    }
    
}
