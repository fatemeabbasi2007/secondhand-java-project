package org.example.frontend.network;

public class SessionManager {
    private static String jwtToken = null;
    private static String role = null;
    private static int userId = -1;
    private static String username = null;

    public static void setSession(String token, String userRole, int id, String name) {
        jwtToken = token;
        role = userRole;
        userId = id;
        username = name;
    }

    public static String getToken() { return jwtToken; }
    public static String getRole() { return role; }
    public static int getUserId() { return userId; }
    public static String getUsername() { return username; }

    public static boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    public static void clearSession() {
        jwtToken = null;
        role = null;
        userId = -1;
        username = null;
    }

    public static boolean isLoggedIn() {
        return jwtToken != null;
    }
}