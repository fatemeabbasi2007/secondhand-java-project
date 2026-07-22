package org.example.frontend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.frontend.config.ApiClient;
import org.example.frontend.config.ApiConfig;
import org.example.frontend.model.AdResponse;
import org.example.frontend.model.ErrorResponse;
import org.example.frontend.model.PendingAdResponse;
import org.example.frontend.model.UserResponse;
import org.example.frontend.security.SessionManager;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AdminService {

    private final HttpClient client;
    private final ObjectMapper objectMapper;

    public AdminService() {
        // ۱. استفاده از کلاینت مشترک همراه با کوکی سشن
        this.client = ApiClient.getClient();
        this.objectMapper = new ObjectMapper();
    }

    // ۱. دریافت لیست آگهی‌های در انتظار بررسی
    public List<PendingAdResponse> getPendingAdvertisements() throws Exception {
        String userId = SessionManager.getInstance().getUserId();
        if (userId == null) {
            throw new Exception("شما به عنوان مدیر وارد سیستم نشده‌اید.");
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/advertisements/admin/pending"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), new TypeReference<List<PendingAdResponse>>() {});
        } else {
            handleErrorResponse(response);
            return null;
        }
    }

    // متد تایید آگهی
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

    // متد رد آگهی
    public void rejectAdvertisement(String adId, String reason) throws Exception {
        String encodedReason = URLEncoder.encode(reason, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/advertisements/admin/" + adId + "/reject?reason=" + encodedReason))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            handleErrorResponse(response);
        }
    }

    // دریافت لیست تمام کاربران سیستم
    public List<UserResponse> getAllUsers() throws Exception {
        String userId = SessionManager.getInstance().getUserId();
        if (userId == null) throw new Exception("شما به عنوان مدیر وارد سیستم نشده‌اید.");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/users/admin/all-users"))
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

    // ۲. متد تغییر وضعیت مسدود بودن کاربر (تغییر نوع userId از Long به String)
    public void toggleUserBlockStatus(String userId, boolean block) throws Exception {
        String action = block ? "/block/" : "/unblock/";
        String fullPath = "/api/users/admin" + action + userId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + fullPath))
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            handleErrorResponse(response);
        }
    }

    // ۳. متد حذف آگهی نامناسب (تغییر نوع adId از Long به String)
    public void deleteInappropriateAdvertisement(String adId) throws Exception {
        String userId = SessionManager.getInstance().getUserId();
        if (userId == null) throw new Exception("شما به عنوان مدیر وارد سیستم نشده‌اید.");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/advertisements/admin/" + adId))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 204) {
            handleErrorResponse(response);
        }
    }

    // for delete
    private final PublicAdService publicAdService = new PublicAdService();

    public List<AdResponse> getAllActiveAdvertisements() throws Exception {
        // دریافت تمام آگهی‌های فعال بدون اعمال هیچ فیلتری
        return publicAdService.getActiveAdvertisements(null, null, null, null, null);[span_0](start_span)[span_0](end_span)[span_1](start_span)[span_1](end_span)
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
}