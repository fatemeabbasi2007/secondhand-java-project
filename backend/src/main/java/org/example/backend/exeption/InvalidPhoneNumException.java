package org.example.backend.exeption;

public class InvalidPhoneNumException extends RuntimeException {
    public InvalidPhoneNumException(String message) {
        super(message);
    }
}
