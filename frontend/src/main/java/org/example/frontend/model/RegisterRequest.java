package org.example.frontend.model;

public class RegisterRequest {
    private String fullName;
    private String email;
    private String phoneNumber; // مطابق با نام فیلدی که بک‌اند انتظار دارد
    private String username;
    private String password;

    public RegisterRequest() {} // سازنده خالی برای جکسون الزامی است

    public RegisterRequest(String fullName, String email, String phoneNumber, String username, String password) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.password = password;
    }

    // گترها و سترها
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}