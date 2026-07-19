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
import java.util.List;

public class AdvertisementService {

    private final HttpClient client;
    private final ObjectMapper objectMapper;

    public AdvertisementService() {
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public void createAdvertisement(String title, String description, double price, String city, String category, List<String> imageUrls) throws Exception {
        // دریافت توکن کاربر از سشن
        String token = SessionManager.getInstance().getToken();
        if (token == null) {
            throw new Exception("شما وارد حساب کاربری خود نشده‌اید.");
        }

        // ۱. ساخت بخش آدرس متنی به همراه کوری‌پارامترهای عکس
        // ساختار آدرس به این شکل می‌شود: /api/advertisements/create?imageUrls=url1&imageUrls=url2
        StringBuilder urlBuilder = new StringBuilder(ApiConfig.BASE_URL + "/api/advertisements/create");

        if (imageUrls != null && !imageUrls.isEmpty()) {
            urlBuilder.append("?");
            for (int i = 0; i < imageUrls.size(); i++) {
                urlBuilder.append("imageUrls=").append(java.net.URLEncoder.encode(imageUrls.get(i), "UTF-8"));
                if (i < imageUrls.size() - 1) {
                    urlBuilder.append("&");
                }
            }
        }

        // ۲. آماده‌سازی بدنه جی‌سان فقط برای اطلاعات متنی آگهی
        AdvertisementRequest adRequest = new AdvertisementRequest(title, description, price, city, category);
        String jsonBody = objectMapper.writeValueAsString(adRequest);

        // ۳. ساخت درخواست HTTP POST (بدون توکن و هدر احراز هویت)
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBuilder.toString()))
                .header("Content-Type", "application/json")
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
    public void updateAdvertisement(Long adId, String title, String description, double price, String city, String category, List<String> imageUrls) throws Exception {
        //  بررسی لاگین بودن کاربر از روی سشن
        Long userId = SessionManager.getInstance().getUserId();
        if (userId == null) {
            throw new Exception("شما وارد حساب کاربری خود نشده‌اید.");
        }

        //  ساخت شیء درخواست به همراه آیدی آگهی
        AdvertisementRequest adRequest = new AdvertisementRequest(adId.toString(), title, description, price, city, category, imageUrls);
        String jsonBody = objectMapper.writeValueAsString(adRequest);

        // ۳. ارسال درخواست PUT کاملاً سشن‌محور و بدون توکن هدر
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/advertisements/own/" + adId))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // ۴. بررسی موفقیت‌آمیز بودن پاسخ
        if (response.statusCode() != 200) {
            try {
                ErrorResponse error = objectMapper.readValue(response.body(), ErrorResponse.class);
                throw new Exception(error.getMessage());
            } catch (Exception e) {
                throw new Exception("خطا در ویرایش آگهی، کد وضعیت: " + response.statusCode());
            }
        }
    }
}