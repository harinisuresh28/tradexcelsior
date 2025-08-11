package com.tp.tradexcelsior.exception.custom;

public class ReferenceAlreadyExistException extends RuntimeException {
    public ReferenceAlreadyExistException(String message) {
        super(message);
    }
}