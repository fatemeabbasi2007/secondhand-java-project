package org.example.backend.exeption;

public class TitleInvalidException extends RuntimeException {
    public TitleInvalidException(String message) {
        super(message);
    }
}
