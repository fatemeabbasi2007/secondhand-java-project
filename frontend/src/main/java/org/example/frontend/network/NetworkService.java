package org.example.frontend.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.frontend.model.LoginRequest;
import org.example.frontend.model.RegisterRequest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class NetworkService {

    // آدرس پایه API بک‌اندمان
    private static final String BASE_URL = "http://localhost:8080/api";

    private final HttpClient httpClient;

    // جکسون برای تبدیل خودکار کلاس‌های جاوا به متن JSON
    private final ObjectMapper objectMapper;

    public NetworkService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * ۱. متد ارسال درخواست ورود (Login) به بک‌اند
     */
    public HttpResponse<String> login(String username, String password) throws Exception {
        String url = BASE_URL + "/auth/login";

        // ساخت شیء درخواست لاگین
        LoginRequest loginRequest = new LoginRequest(username, password);

        // جکسون این شیء را خودکار به فرمت استاندارد {"username":"...", "password":"..."} تبدیل می‌کند
        String jsonBody = objectMapper.writeValueAsString(loginRequest);

        // ساخت درخواست HTTP POST
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        // ارسال به سرور و برگرداندن پاسخ
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * ۲. متد ارسال درخواست ثبت‌نام (Register) به بک‌اند
     */
    public HttpResponse<String> register(String fullName, String email, String phone, String username, String password) throws Exception {
        String url = BASE_URL + "/auth/register";

        // ساخت شیء درخواست ثبت‌نام (بدون دردسر فرمت دستی JSON)
        RegisterRequest registerRequest = new RegisterRequest(fullName, email, phone, username, password);

        // تبدیل خودکار کل اطلاعات ثبت‌نام به JSON توسط جکسون
        String jsonBody = objectMapper.writeValueAsString(registerRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}