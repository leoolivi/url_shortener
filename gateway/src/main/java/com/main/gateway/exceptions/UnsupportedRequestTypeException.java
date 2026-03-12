package com.main.gateway.exceptions;

public class UnsupportedRequestTypeException extends RuntimeException {
    public UnsupportedRequestTypeException(String msg){
        super(msg);
    }
}
