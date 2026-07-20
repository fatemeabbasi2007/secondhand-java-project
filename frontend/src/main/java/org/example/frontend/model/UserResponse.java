package org.example.frontend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse {

    private String id;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String role;

    // بک‌اند فیلد enabled را می‌فرستد (true = فعال، false = مسدود)
    @JsonProperty("enabled")
    private boolean enabled = true;

    public UserResponse() {}

    // --- منطق وضعیت مسدود بودن ---
    // اگر enabled برابر false باشد، یعنی کاربر مسدود شده است
    public boolean isBlocked() {
        return !enabled;
    }

    public void setBlocked(boolean blocked) {
        this.enabled = !blocked;
    }

    // --- Getterها و Setterهای استاندارد ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}