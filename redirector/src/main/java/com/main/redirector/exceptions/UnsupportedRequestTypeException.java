package com.main.redirector.exceptions;

public class UnsupportedRequestTypeException extends RuntimeException {
    public UnsupportedRequestTypeException(String msg){
        super(msg);
    }
}
