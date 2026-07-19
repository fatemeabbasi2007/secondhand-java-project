package org.example.frontend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.frontend.config.ApiConfig;
import org.example.frontend.model.AdminReviewRequest;
import org.example.frontend.model.ErrorResponse;
import org.example.frontend.model.PendingAdResponse;
import org.example.frontend.security.SessionManager;
import org.example.frontend.model.UserResponse;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class AdminService {

    private final HttpClient client;
    private final ObjectMapper objectMapper;

    public AdminService() {
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    // ۱. دریافت لیست آگهی‌های در انتظار بررسی
    public List<PendingAdResponse> getPendingAdvertisements() throws Exception {
        String token = SessionManager.getInstance().getToken();
        if (token == null) {
            throw new Exception("شما به عنوان مدیر وارد سیستم نشده‌اید.");
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/advertisements/admin/pending"))
                //.header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            // تبدیل آرایه JSON به لیست پویایی از اشیاء PendingAdResponse
            return objectMapper.readValue(response.body(), new TypeReference<List<PendingAdResponse>>() {});
        } else {
            handleErrorResponse(response);
            return null;
        }
    }

    // ۱. متد تایید آگهی
    public void approveAdvertisement(String adId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/advertisements/admin/" + adId + "/approve"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            handleErrorResponse(response);
        }
    }

    // ۲. متد رد آگهی
    public void rejectAdvertisement(String adId, String reason) throws Exception {
        // تبدیل دلیل رد به فرمت مناسب URL (برای فضاها و کاراکترهای خاص)
        String encodedReason = java.net.URLEncoder.encode(reason, java.nio.charset.StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/advertisements/admin/" + adId + "/reject?reason=" + encodedReason))
                .POST(HttpRequest.BodyPublishers.noBody()) // پارامترها در URL فرستاده می‌شوند
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            handleErrorResponse(response);
        }
    }

    private void handleErrorResponse(HttpResponse<String> response) throws Exception {
        try {
            ErrorResponse error = objectMapper.readValue(response.body(), ErrorResponse.class);
            throw new Exception(error.getMessage());
        } catch (Exception e) {
            if (response.body() != null && !response.body().isBlank()) {
                throw new Exception(response.body());
            }
            throw new Exception("خطایی در عملیات مدیریت رخ داد. کد وضعیت: " + response.statusCode());
        }
    }

    // متد دریافت لیست تمام کاربران سیستم (برای بخش مدیریت کاربران)
    public List<UserResponse> getAllUsers() throws Exception {
        String token = SessionManager.getInstance().getToken();
        if (token == null) throw new Exception("شما به عنوان مدیر وارد سیستم نشده‌اید.");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/users/admin/all-users"))
                //.header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), new TypeReference<List<UserResponse>>() {});
        } else {
            handleErrorResponse(response);
            return null;
        }
    }

    // متد تغییر وضعیت مسدود بودن کاربر (مسدود یا رفع مسدود کردن)
    public void toggleUserBlockStatus(Long userId, boolean block) throws Exception {
        // ۱. بر اساس وضعیت boolean، مسیر درست را مشخص می‌کنیم (آی‌دی کاربر به انتهای آدرس می‌رود)
        String action = block ? "/block/" : "/unblock/";
        String fullPath = "/api/users/admin" + action + userId;

        // ۲. ساخت درخواست PATCH بدون بادی و توکن
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + fullPath))
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();

        // ۳. ارسال درخواست
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            handleErrorResponse(response);
        }
    }

    // متد حذف آگهی نامناسب (با استفاده از متد DELETE )
    public void deleteInappropriateAdvertisement(Long adId) throws Exception {
        String token = SessionManager.getInstance().getToken();
        if (token == null) throw new Exception("شما به عنوان مدیر وارد سیستم نشده‌اید.");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/advertisements/admin/" + adId))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 204) {
            handleErrorResponse(response);
        }
    }
}