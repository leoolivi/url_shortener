package com.main.gateway.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestController {
    @GetMapping("test")
    public String test(@PathVariable String param) {
        return "Test parametro: " + param;
    }
    
}
