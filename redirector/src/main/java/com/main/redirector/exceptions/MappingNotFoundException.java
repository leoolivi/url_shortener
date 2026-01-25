package com.main.redirector.exceptions;

public class MappingNotFoundException extends RuntimeException {
    public MappingNotFoundException(String msg) {
        super(msg);
    }
}
