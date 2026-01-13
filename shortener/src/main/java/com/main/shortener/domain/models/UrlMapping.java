package com.main.shortener.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "mappings")
public class UrlMapping {
    @Column(unique=true)
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    @Column(unique=true, nullable=false)
    private String shortMapping;
    @Column(unique=false, nullable=false)
    private String pointingUrl;
}
