package com.demo.subhanfinalproject.exception.custom;

public class CardIsNotActiveException extends RuntimeException {
    public CardIsNotActiveException(String message) {
        super(message);
    }
}
