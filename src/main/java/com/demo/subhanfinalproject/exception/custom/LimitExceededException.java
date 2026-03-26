package com.demo.subhanfinalproject.exception.custom;

public class LimitExceededException extends RuntimeException {
    public LimitExceededException(String message) {
        super(message);
    }
}
