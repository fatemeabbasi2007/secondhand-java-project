package org.example.frontend.model;

public class UserResponse {
    private Long id;
    private String fullName;
    private String username;
    private String phoneNum;
    private String email;
    private String role;
    private boolean enable; // وضعیت مسدود بودن کاربر

    public UserResponse() {}

    public UserResponse(Long id, String fullName, String username, String phoneNumber, String email, String role, boolean blocked) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.phoneNum = phoneNumber;
        this.email = email;
        this.role = role;
        this.enable = blocked;
    }

    // متدهای Getter و Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPhoneNumber() { return phoneNum; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNum = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isBlocked() { return enable; }
    public void setBlocked(boolean blocked) { this.enable = blocked; }
}