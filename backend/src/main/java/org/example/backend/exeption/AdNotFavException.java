package org.example.backend.exeption;

public class AdNotFavException extends RuntimeException {
    public AdNotFavException(String message) {
        super(message);
    }
}
