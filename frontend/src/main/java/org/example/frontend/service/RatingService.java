package org.example.frontend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.frontend.config.ApiConfig;
import org.example.frontend.model.ErrorResponse;
import org.example.frontend.model.RateSellerRequest;
import org.example.frontend.security.SessionManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RatingService {

    private final HttpClient client;
    private final ObjectMapper objectMapper;

    public RatingService() {
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    // ارسال امتیاز به بک‌اند (مراحل ۵ تا ۹ سناریو)
    public void rateSeller(Long adId, int rating, String comment) throws Exception {
        String token = SessionManager.getInstance().getToken();
        if (token == null) {
            throw new Exception("برای ثبت امتیاز ابتدا باید وارد حساب کاربری خود شوید.");
        }

        RateSellerRequest rateRequest = new RateSellerRequest(adId, rating, comment);
        String jsonBody = objectMapper.writeValueAsString(rateRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/ratings"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 201) {
            try {
                ErrorResponse error = objectMapper.readValue(response.body(), ErrorResponse.class);
                throw new Exception(error.getMessage());
            } catch (Exception e) {
                if (response.body() != null && !response.body().isBlank()) {
                    throw new Exception(response.body());
                }
                throw new Exception("خطا در ثبت امتیاز. کد وضعیت: " + response.statusCode());
            }
        }
    }
}