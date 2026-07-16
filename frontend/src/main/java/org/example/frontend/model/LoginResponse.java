package org.example.frontend.model;

public class LoginResponse {
    private String token;
    private int userId;
    private String username;
    private String role;

    public LoginResponse() {} // سازنده خالی برای جکسون

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}