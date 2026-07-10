package org.example.backend.exeption;

public class AdAlreadyFavoriteException extends RuntimeException {
    public AdAlreadyFavoriteException(String message) {
        super(message);
    }
}
