package org.example.backend.controller;

public class ErrorResponse {

    private String error;

    // ۱. سازنده (Constructor) برای مقداردهی راحت در کنترلر
    public ErrorResponse(String error) {
        this.error = error;
    }

    // ۲. متد Getter (اسپرینگ‌بوت برای تبدیل این کلاس به JSON به این گتر نیاز دارد)
    public String getError() {
        return error;
    }

    // ۳. متد Setter
    public void setError(String error) {
        this.error = error;
    }
}