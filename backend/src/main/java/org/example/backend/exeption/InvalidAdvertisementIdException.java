package org.example.backend.exeption;

public class InvalidAdvertisementIdException extends RuntimeException {
    public InvalidAdvertisementIdException(String message) {
        super(message);
    }
}
