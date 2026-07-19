package org.example.frontend.security;

public class SessionManager {
    private static SessionManager instance;

    private String token;
    private Long userId;
    private String username;
    private String role;

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // ذخیره اطلاعات پس از ورود موفق
    public void createSession(String token, Long userId, String username, String role) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    // پاک کردن اطلاعات در زمان خروج از حساب
    public void cleanSession() {
        this.token = null;
        this.userId = null;
        this.username = null;
        this.role = null;
    }

    public boolean isLoggedIn() {
        return this.token != null;
    }

    // متدهای Getter
    public String getToken() { return token; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
}