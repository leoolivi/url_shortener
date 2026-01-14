package com.main.shortener.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No such Mapping") 
public class MappingNotFoundException extends RuntimeException {
    public MappingNotFoundException(String msg) {
        super(msg);
    }
}
