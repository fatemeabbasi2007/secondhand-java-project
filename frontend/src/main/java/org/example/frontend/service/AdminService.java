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

    // ۱. دریافت لیست آگهی‌های در انتظار بررسی (مرحله ۲ سناریو)
    public List<PendingAdResponse> getPendingAdvertisements() throws Exception {
        String token = SessionManager.getInstance().getToken();
        if (token == null) {
            throw new Exception("شما به عنوان مدیر وارد سیستم نشده‌اید.");
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/admin/advertisements/pending"))
                .header("Authorization", "Bearer " + token)
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

    // ۲. تایید یا رد آگهی (مراحل ۴ تا ۷ سناریو)
    public void reviewAdvertisement(Long adId, boolean approved, String rejectionReason) throws Exception {
        String token = SessionManager.getInstance().getToken();
        if (token == null) {
            throw new Exception("شما به عنوان مدیر وارد سیستم نشده‌اید.");
        }

        AdminReviewRequest reviewRequest = new AdminReviewRequest(approved, rejectionReason);
        String jsonBody = objectMapper.writeValueAsString(reviewRequest);

        // بر اساس طراحی بک‌اند، مسیر تایید معمولاً به شکل زیر است:
        // PUT /api/admin/advertisements/{id}/review
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/admin/advertisements/" + adId + "/review"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 204) {
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
                .uri(URI.create(ApiConfig.BASE_URL + "/api/admin/users"))
                .header("Authorization", "Bearer " + token)
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
        String token = SessionManager.getInstance().getToken();
        if (token == null) throw new Exception("شما به عنوان مدیر وارد سیستم نشده‌اید.");

        // بر اساس متدهای استاندارد HTTP، ویرایش وضعیت کاربر معمولاً با PUT است
        String endpoint = block ? "/block" : "/unblock";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/admin/users/" + userId + endpoint))
                .header("Authorization", "Bearer " + token)
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 204) {
            handleErrorResponse(response);
        }
    }

    // متد حذف آگهی نامناسب (با استفاده از متد DELETE بر اساس مستندات پروژه)
    public void deleteInappropriateAdvertisement(Long adId) throws Exception {
        String token = SessionManager.getInstance().getToken();
        if (token == null) throw new Exception("شما به عنوان مدیر وارد سیستم نشده‌اید.");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/admin/advertisements/" + adId))
                .header("Authorization", "Bearer " + token)
                .DELETE() // استفاده از متد DELETE برای حذف آگهی نامناسب
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 204) {
            handleErrorResponse(response);
        }
    }
}