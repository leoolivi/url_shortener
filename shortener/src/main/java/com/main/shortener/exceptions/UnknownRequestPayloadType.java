package com.main.shortener.exceptions;

public class UnknownRequestPayloadType extends RuntimeException {

    public UnknownRequestPayloadType(String msg) {
        super(msg);
    }
    
}
