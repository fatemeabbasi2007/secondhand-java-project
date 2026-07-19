package org.example.frontend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.frontend.config.ApiConfig;
import org.example.frontend.model.ErrorResponse;
import org.example.frontend.model.LoginRequest;
import org.example.frontend.model.LoginResponse;
import org.example.frontend.model.RegisterRequest;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthService {

    private final HttpClient client;
    private final ObjectMapper objectMapper; // ابزار تبدیل شیء به JSON و برعکس

    public AuthService() {
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public HttpResponse<String> register(String name, String username, String password, String phone, String email) throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(name, username, password, phone, email);
        String jsonBody = objectMapper.writeValueAsString(registerRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/users/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 201 || response.statusCode() == 200) {
            return response;
        } else {
            try {
                ErrorResponse error = objectMapper.readValue(response.body(), ErrorResponse.class);
                throw new Exception(error.getMessage());
            } catch (Exception e) {
                if (response.body() != null && !response.body().isBlank()) {
                    throw new Exception(response.body());
                }
                throw new Exception("خطایی در ثبت‌نام رخ داد. کد خطا: " + response.statusCode());
            }
        }
    }

    public LoginResponse login(String username, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest(username, password);
        String jsonBody = objectMapper.writeValueAsString(loginRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/users/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            // ورود موفق
            return objectMapper.readValue(response.body(), LoginResponse.class);
        } else {
            try {
                // تلاش برای خواندن بدنه خطا به عنوان ErrorResponse استاندارد سرور
                ErrorResponse error = objectMapper.readValue(response.body(), ErrorResponse.class);
                throw new Exception(error.getMessage());
            } catch (Exception e) {
                // اگر فرمت پاسخ خطا JSON نبود یا خواندنش خطا داد، کل متن پاسخ یا یک پیام پیش‌فرض را می‌فرستیم
                if (response.body() != null && !response.body().isBlank()) {
                    throw new Exception(response.body());
                }
                throw new Exception("خطای ناشناخته از سمت سرور با کد: " + response.statusCode());
            }
        }
    }

    public void logout() throws Exception {
        // ارسال درخواست POST به سرور برای باطل کردن سشن
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/users/logout"))
                .POST(HttpRequest.BodyPublishers.noBody()) // چون متد @PostMapping است و بدنه نمی‌خواهد
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // اگر سرور خطا داد (مثلاً کاربر اصلاً لاگین نبوده)
        if (response.statusCode() != 200) {
            try {
                ErrorResponse error = objectMapper.readValue(response.body(), ErrorResponse.class);
                throw new Exception(error.getMessage());
            } catch (Exception e) {
                throw new Exception("خطا در خروج از سیستم. کد وضعیت: " + response.statusCode());
            }
        }
    }
}