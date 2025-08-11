package com.tp.tradexcelsior.exception.custom;

public class UserNameAlreadyExistsException extends RuntimeException {
  public UserNameAlreadyExistsException(String message) {
    super(message);
  }
}

