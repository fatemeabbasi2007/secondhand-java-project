package org.example.frontend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.frontend.config.ApiClient;
import org.example.frontend.config.ApiConfig;
import org.example.frontend.model.AdDetailsResponse;
import org.example.frontend.model.AdResponse;
import org.example.frontend.model.ErrorResponse;
import org.example.frontend.security.SessionManager;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class PublicAdService {

    private final HttpClient client;
    private final ObjectMapper objectMapper;

    public PublicAdService() {
        // ۱. استفاده از ApiClient مشترک برای ارسال کوکی سشن
        this.client = ApiClient.getClient();
        this.objectMapper = new ObjectMapper();
    }

    // دریافت لیست آگهی‌های فعال با قابلیت جست‌وجو و فیلتر
    public List<AdResponse> getActiveAdvertisements(String query, String category, String city, Double minPrice, Double maxPrice) throws Exception {

        StringBuilder urlBuilder = new StringBuilder(ApiConfig.BASE_URL + "/api/advertisements/search?");

        if (query != null && !query.isBlank()) {
            urlBuilder.append("keyword=").append(URLEncoder.encode(query, StandardCharsets.UTF_8)).append("&");
        }

        if (category != null && !category.isBlank()) {
            urlBuilder.append("categoryId=").append(URLEncoder.encode(category, StandardCharsets.UTF_8)).append("&");
        }

        if (city != null && !city.isBlank()) {
            urlBuilder.append("city=").append(URLEncoder.encode(city, StandardCharsets.UTF_8)).append("&");
        }
        if (minPrice != null) {
            urlBuilder.append("minPrice=").append(minPrice).append("&");
        }
        if (maxPrice != null) {
            urlBuilder.append("maxPrice=").append(maxPrice).append("&");
        }

        String finalUrl = urlBuilder.toString();
        if (finalUrl.endsWith("&") || finalUrl.endsWith("?")) {
            finalUrl = finalUrl.substring(0, finalUrl.length() - 1);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(finalUrl))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), new TypeReference<List<AdResponse>>() {});
        } else {
            try {
                ErrorResponse error = objectMapper.readValue(response.body(), ErrorResponse.class);
                throw new Exception(error.getMessage());
            } catch (Exception e) {
                throw new Exception("خطا در دریافت لیست آگهی‌ها، کد وضعیت: " + response.statusCode());
            }
        }
    }

    // ۲. تغییر نوع adId از Long به String
    public AdDetailsResponse getAdvertisementDetails(String adId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/advertisements/" + adId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), AdDetailsResponse.class);
        } else {
            try {
                ErrorResponse error = objectMapper.readValue(response.body(), ErrorResponse.class);
                throw new Exception(error.getMessage());
            } catch (Exception e) {
                throw new Exception("خطا در دریافت جزئیات آگهی. کد وضعیت: " + response.statusCode());
            }
        }
    }

    // ۳. تغییر نوع adId به String و کنترل ورود از روی userId
    public void deleteAdvertisement(String adId) throws Exception {
        String userId = SessionManager.getInstance().getUserId();
        if (userId == null) throw new Exception("جهت انجام عملیات ابتدا وارد حساب خود شوید.");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/advertisements/own/" + adId))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 204) {
            try {
                ErrorResponse error = objectMapper.readValue(response.body(), ErrorResponse.class);
                throw new Exception(error.getMessage());
            } catch (Exception e) {
                throw new Exception("خطا در حذف آگهی. کد وضعیت: " + response.statusCode());
            }
        }
    }

    // ۴. تغییر نوع adId به String
    public void markAsSold(String adId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/advertisements/own/" + adId + "/sold"))
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            try {
                ErrorResponse error = objectMapper.readValue(response.body(), ErrorResponse.class);
                throw new Exception(error.getMessage());
            } catch (Exception e) {
                throw new Exception("خطا در تغییر وضعیت آگهی. کد وضعیت: " + response.statusCode());
            }
        }
    }
}