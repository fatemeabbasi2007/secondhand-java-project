package org.example.frontend.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) // برای نادیده گرفتن فیلدهای اضافی مثل email و password
public class LoginResponse {
    private String token;

    @JsonAlias("id")
    private String userId; // <--- تغییر از Long به String

    private String username;
    private String role;

    public LoginResponse() {}

    public LoginResponse(String token, String userId, String username, String role) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUserId() { return userId; } // <--- خروجی String
    public void setUserId(String userId) { this.userId = userId; } // <--- ورودی String

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}