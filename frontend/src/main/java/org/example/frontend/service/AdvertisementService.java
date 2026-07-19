package org.example.frontend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.frontend.config.ApiConfig;
import org.example.frontend.model.AdvertisementRequest;
import org.example.frontend.model.ErrorResponse;
import org.example.frontend.security.SessionManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AdvertisementService {

    private final HttpClient client;
    private final ObjectMapper objectMapper;

    public AdvertisementService() {
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public void createAdvertisement(String title, String description, double price, String city, String category) throws Exception {
        // دریافت توکن کاربر از سشن
        String token = SessionManager.getInstance().getToken();
        if (token == null) {
            throw new Exception("شما وارد حساب کاربری خود نشده‌اید.");
        }

        AdvertisementRequest adRequest = new AdvertisementRequest(title, description, price, city, category);
        String jsonBody = objectMapper.writeValueAsString(adRequest);

        // ساخت درخواست همراه با هدر Authorization و توکن JWT
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/advertisements/create"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token) // ارسال توکن در درخواست
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // اگر کد وضعیت 201 (ساخته شد) یا 200 نبود، خطای سرور را پردازش می‌کنیم
        if (response.statusCode() != 201 && response.statusCode() != 200) {
            try {
                ErrorResponse error = objectMapper.readValue(response.body(), ErrorResponse.class);
                throw new Exception(error.getMessage());
            } catch (Exception exception) {
                if (response.body() != null && !response.body().isBlank()) {
                    throw new Exception(response.body());
                }
                throw new Exception("خطا در ثبت آگهی. کد وضعیت: " + response.statusCode());
            }
        }
    }

    // ارسال درخواست ویرایش آگهی به بک‌اند با متد PUT
    public void updateAdvertisement(Long adId, String title, String description, double price, String city, String category) throws Exception {
        String token = SessionManager.getInstance().getToken();
        if (token == null) {
            throw new Exception("شما وارد حساب کاربری خود نشده‌اید.");
        }

        // ساخت شیء درخواست با اطلاعات جدید
        org.example.frontend.model.AdvertisementRequest adRequest =
                new org.example.frontend.model.AdvertisementRequest(title, description, price, city, category);
        String jsonBody = objectMapper.writeValueAsString(adRequest);

        // ارسال درخواست PUT به همراه آی‌دی آگهی در آدرس URL
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/advertisements/own/" + adId))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody)) // استفاده از PUT برای ویرایش
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 204) {
            try {
                ErrorResponse error = objectMapper.readValue(response.body(), ErrorResponse.class);
                throw new Exception(error.getMessage());
            } catch (Exception e) {
                throw new Exception("خطا در ویرایش آگهی. کد وضعیت: " + response.statusCode());
            }
        }
    }
}