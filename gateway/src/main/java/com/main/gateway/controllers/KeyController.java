package com.main.gateway.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.gateway.services.InternalKeyPairService;

import lombok.AllArgsConstructor;


@RestController
@RequestMapping("/api/v1/keys")
@AllArgsConstructor
public class KeyController {

    private final InternalKeyPairService keyPairService;
    
/*     @PostMapping("internal/public")
    public ResponseEntity<PublicKeyResponse> getInternalPublicKey() throws NoSuchAlgorithmException {
        var kp = keyPairService.generateStringKeyPair();
        return ResponseEntity.ok(new PublicKeyResponse(kp.getPublicKey()));
    } */
    
}
