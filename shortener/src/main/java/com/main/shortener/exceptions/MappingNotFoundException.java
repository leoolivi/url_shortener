package com.main.shortener.exceptions;

public class MappingNotFoundException extends RuntimeException {
    public MappingNotFoundException(String msg) {
        super(msg);
    }
}
