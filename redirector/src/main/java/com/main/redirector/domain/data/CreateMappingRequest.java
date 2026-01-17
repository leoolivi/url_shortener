package com.main.redirector.domain.data;

public record CreateMappingRequest (
    String code,
    String originalUrl
) 
{}
