package com.main.redirector.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/test")
@AllArgsConstructor
public class TestController {
    
    @GetMapping("/")
    public String root(@RequestParam String username) {
        return "Hello user, " + username;
    }
}
