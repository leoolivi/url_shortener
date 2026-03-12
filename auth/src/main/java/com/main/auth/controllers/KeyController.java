package com.main.auth.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.auth.services.UserKeyPairService;

import lombok.AllArgsConstructor;


@RestController
@RequestMapping("/api/v1/keys")
@AllArgsConstructor
public class KeyController {

    private final UserKeyPairService keyPairService;
    
    /* @PostMapping("user/public")
    public ResponseEntity<PublicKeyResponse> getUserPublicKey() throws NoSuchAlgorithmException {
        var kp = keyPairService.generateStringKeyPair();
        return ResponseEntity.ok(new PublicKeyResponse(kp.getPublicKey()));
    } */
    
}
