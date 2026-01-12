package com.main.shortener.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/test")
public class TestController {
    
    @GetMapping("/")
    public String root(@RequestParam String username) {
        return "Hello user, " + username;
    }
    
}
