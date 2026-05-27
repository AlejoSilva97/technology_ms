package com.example.technology.domain.exceptions;

public class TechnologyAlreadyExistsException extends RuntimeException {
    public TechnologyAlreadyExistsException(String message) {
        super(message);
    }
}
