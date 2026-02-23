package com.main.shortener.exceptions;

public class UnsupportedRequestTypeException extends RuntimeException {
    public UnsupportedRequestTypeException(String msg){
        super(msg);
    }
}
