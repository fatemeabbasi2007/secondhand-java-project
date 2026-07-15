package org.example.frontend.network;

public class SessionManager {
    // توکن کاربر در این متغیر ذخیره می‌شود
    private static String jwtToken = null;

    public static void setToken(String token) {
        jwtToken = token;
    }

    public static String getToken() {
        return jwtToken;
    }

    public static void clearSession() {
        jwtToken = null;
    }

    public static boolean isLoggedIn() {
        return jwtToken != null;
    }
}