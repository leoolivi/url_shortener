package com.main.shortener.exceptions;

public class MappingAlreadyExistException extends RuntimeException {

    public MappingAlreadyExistException(String msg) {
        super(msg);
    }
    
}
