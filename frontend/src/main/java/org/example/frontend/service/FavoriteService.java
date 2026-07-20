package org.example.frontend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.frontend.config.ApiClient;
import org.example.frontend.config.ApiConfig;
import org.example.frontend.model.ErrorResponse;
import org.example.frontend.model.FavoriteResponse;
import org.example.frontend.security.SessionManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class FavoriteService {

    private final HttpClient client;
    private final ObjectMapper objectMapper;

    public FavoriteService() {
        // ۱. استفاده از ApiClient مشترک برای مدیریت کوکی سشن
        this.client = ApiClient.getClient();
        this.objectMapper = new ObjectMapper();
    }

    // افزودن آگهی به علاقه‌مندی‌ها
    public void addToFavorites(String adId) throws Exception {
        String userId = SessionManager.getInstance().getUserId();
        if (userId == null) throw new Exception("لطفاً ابتدا وارد حساب کاربری خود شوید.");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/users/" + userId + "/favorites/" + adId))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 201) {
            handleErrorResponse(response);
        }
    }

    // دریافت لیست علاقه‌مندی‌های کاربر
    public List<FavoriteResponse> getFavorites() throws Exception {
        String userId = SessionManager.getInstance().getUserId();
        if (userId == null) throw new Exception("لطفاً ابتدا وارد حساب کاربری خود شوید.");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/users/" + userId + "/favorites"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), new TypeReference<List<FavoriteResponse>>() {});
        } else {
            handleErrorResponse(response);
            return null;
        }
    }

    // ۲. تغییر نوع favoriteId از Long به String
    public void removeFromFavorites(String favoriteId) throws Exception {
        String userId = SessionManager.getInstance().getUserId();
        if (userId == null) throw new Exception("لطفاً ابتدا وارد حساب کاربری خود شوید.");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/users/" + userId + "/favorites/" + favoriteId))
                .DELETE()
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
            throw new Exception("خطایی در بخش علاقه‌مندی‌ها رخ داد. کد وضعیت: " + response.statusCode());
        }
    }
}