package com.tp.tradexcelsior.exception.custom;

public class ChecklistAlreadyExistException extends RuntimeException {
    public ChecklistAlreadyExistException(String message) {
        super(message);
    }
}