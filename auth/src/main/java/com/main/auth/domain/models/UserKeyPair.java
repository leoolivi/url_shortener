package com.main.auth.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class UserKeyPair {
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    @Lob
    @Column(columnDefinition="LONGTEXT")
    private String privateKey;

    @Lob
    @Column(columnDefinition="LONGTEXT")
    private String publicKey;

    @Builder.Default
    private Long issuedAt = System.currentTimeMillis();

    private Long expiresAt;

    @Builder.Default
    private boolean isValid = true;
}
