package org.example.frontend.model;

public class RegisterRequest {
    private String fullName;
    private String username;
    private String password;
    private String phoneNumber;
    private String email;

    // ۱. سازنده خالی (الزامی برای کتابخانه‌های پردازش JSON)
    public RegisterRequest() {
    }

    // ۲. سازنده کامل برای راحتی کار خودمان در کدنویسی
    public RegisterRequest(String fullName, String username, String password, String phoneNumber, String email) {
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    // ۳. متدهای Getter و Setter (الزامی)
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}