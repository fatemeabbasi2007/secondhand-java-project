package org.example.frontend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    // ۱. افزودن آگهی به علاقه‌مندی‌ها (مرحله ۲ و ۳ سناریو)
    public void addToFavorites(Long adId) throws Exception {
        String token = SessionManager.getInstance().getToken();
        if (token == null) throw new Exception("لطفاً ابتدا وارد حساب کاربری خود شوید.");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/favorites/add/" + adId))
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 201) {
            handleErrorResponse(response);
        }
    }

    // ۲. دریافت لیست علاقه‌مندی‌های کاربر (مرحله ۶ سناریو)
    public List<FavoriteResponse> getFavorites() throws Exception {
        String token = SessionManager.getInstance().getToken();
        if (token == null) throw new Exception("لطفاً ابتدا وارد حساب کاربری خود شوید.");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/favorites"))
                .header("Authorization", "Bearer " + token)
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

    // ۳. حذف آگهی از لیست علاقه‌مندی‌ها (مرحله ۷ سناریو)
    public void removeFromFavorites(Long favoriteId) throws Exception {
        String token = SessionManager.getInstance().getToken();
        if (token == null) throw new Exception("لطفاً ابتدا وارد حساب کاربری خود شوید.");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/favorites/" + favoriteId))
                .header("Authorization", "Bearer " + token)
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