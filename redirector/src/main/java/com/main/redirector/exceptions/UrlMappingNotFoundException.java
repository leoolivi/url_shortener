package com.main.redirector.exceptions;

public class UrlMappingNotFoundException extends RuntimeException {
    public UrlMappingNotFoundException(String msg) {
        super(msg);
    }
}
