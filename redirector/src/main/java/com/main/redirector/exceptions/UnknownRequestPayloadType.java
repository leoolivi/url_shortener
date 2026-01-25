package com.main.redirector.exceptions;

public class UnknownRequestPayloadType extends RuntimeException {

    public UnknownRequestPayloadType(String msg) {
        super(msg);
    }
    
}
