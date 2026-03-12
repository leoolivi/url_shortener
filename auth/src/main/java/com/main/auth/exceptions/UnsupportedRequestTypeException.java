package com.main.auth.exceptions;

public class UnsupportedRequestTypeException extends RuntimeException {
    public UnsupportedRequestTypeException(String msg){
        super(msg);
    }
}
