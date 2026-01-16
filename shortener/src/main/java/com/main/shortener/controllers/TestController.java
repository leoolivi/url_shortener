package com.main.shortener.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.main.shortener.messaging.RabbitProducer;

import lombok.AllArgsConstructor;



@RestController
@RequestMapping("/api/v1/test")
@AllArgsConstructor
public class TestController {

    private final RabbitProducer producer;
    
    @GetMapping("/")
    public String root(@RequestParam String username) {
        return "Hello user, " + username;
    }

    @GetMapping("/send-msg-rabbitmq")
    public String sendMsgToRabbitMQ(@RequestParam String msg) {
        producer.sendMessage(msg);
        return msg;
    }
    
    @PostMapping("/send-obj-rabbitmq")
    public Object sendMsgToRabbitMQ(@RequestBody Object obj) {
        producer.sendObject(obj);
        return obj;
    }
}
