package com.main.shortener.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@Builder
@Table(name = "mappings")
@NoArgsConstructor
@AllArgsConstructor
public class UrlMapping {
    @Column(unique=true)
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    @Column(unique=true, nullable=false)
    private String code;
    
    @Column(unique=false, nullable=false)
    private String originalUrl;
    
    @Column(unique=false, nullable=false)
    private Long userId;
}
