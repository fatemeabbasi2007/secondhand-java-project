package org.example.backend.exeption;

public class UserBannedException extends RuntimeException {
    public UserBannedException(String message) {
        super(message);
    }
}
