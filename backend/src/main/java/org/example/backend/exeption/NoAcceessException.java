package org.example.backend.exeption;

public class NoAcceessException extends RuntimeException {
    public NoAcceessException(String message) {
        super(message);
    }
}
