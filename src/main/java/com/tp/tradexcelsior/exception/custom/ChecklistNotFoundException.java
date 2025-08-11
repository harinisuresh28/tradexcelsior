package com.tp.tradexcelsior.exception.custom;

public class ChecklistNotFoundException extends RuntimeException {
    public ChecklistNotFoundException(String message) {
        super(message);
    }
}