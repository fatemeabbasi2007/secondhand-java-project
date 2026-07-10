package org.example.backend.exeption;

public class AdvertisementNotFoundException extends RuntimeException {
    public AdvertisementNotFoundException(String message) {
        super(message);
    }
}
