package com.main.redirector.domain.data;

public record UpdateMappingRequest(
    String code,
    String originalUrl
) {}
