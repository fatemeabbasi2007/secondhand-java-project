package org.example.backend.exeption;

public class PhoneNumAlreadyExistsException extends RuntimeException {
    public PhoneNumAlreadyExistsException(String message) {
        super(message);
    }
}
