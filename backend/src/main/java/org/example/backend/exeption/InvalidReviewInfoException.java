package org.example.backend.exeption;

public class InvalidReviewInfoException extends RuntimeException {
    public InvalidReviewInfoException(String message) {
        super(message);
    }
}
