package org.example.backend.exeption;

public class PriceNegativeException extends RuntimeException {
    public PriceNegativeException(String message) {
        super(message);
    }
}
