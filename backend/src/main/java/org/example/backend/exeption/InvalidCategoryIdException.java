package org.example.backend.exeption;

public class InvalidCategoryIdException extends RuntimeException {
    public InvalidCategoryIdException(String message) {
        super(message);
    }
}
