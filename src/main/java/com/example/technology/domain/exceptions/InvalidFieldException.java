package com.example.technology.domain.exceptions;

public class InvalidFieldException extends RuntimeException{
    public InvalidFieldException(String message) {
        super(message);
    }
}
