package com.tp.tradexcelsior.exception.custom;

public class BookAlreadyExistException extends RuntimeException{
    public BookAlreadyExistException(String message) {
        super(message);
    }
}
