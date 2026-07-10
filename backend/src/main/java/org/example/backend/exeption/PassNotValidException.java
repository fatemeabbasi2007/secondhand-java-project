package org.example.backend.exeption;

public class PassNotValidException extends RuntimeException {
    public PassNotValidException(String message) {
        super(message);
    }
}
