package org.example.frontend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.frontend.config.ApiClient;
import org.example.frontend.config.ApiConfig;
import org.example.frontend.model.*;
import org.example.frontend.security.SessionManager;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ChatService {

    private final HttpClient client;
    private final ObjectMapper objectMapper;

    public ChatService() {
        // ۱. استفاده از کلاینت مشترک همراه با کوکی سشن
        this.client = ApiClient.getClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules(); // 👈 افزودن پشتیبانی از LocalDateTime
    }

    // ۲. ارسال درخواست شروع گفت‌وگو با adId از نوع String
    public void startConversation(String adId, String messageText) throws Exception {
        String userId = SessionManager.getInstance().getUserId();
        if (userId == null) {
            throw new Exception("جهت شروع گفت‌وگو ابتدا باید وارد حساب خود شوید.");
        }

        StartChatRequest chatRequest = new StartChatRequest(adId, messageText);
        String jsonBody = objectMapper.writeValueAsString(chatRequest);

        String url = ApiConfig.BASE_URL + "/api/chats/send?advertisementId=" +
                URLEncoder.encode(adId, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 201) {
            handleErrorResponse(response);
        }
    }

    // دریافت لیست گفت‌وگوهای کاربر
    public List<ConversationResponse> getConversations() throws Exception {
        String userId = SessionManager.getInstance().getUserId();
        if (userId == null) throw new Exception("کاربر وارد سیستم نشده است.");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/chats/conversations"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), new TypeReference<List<ConversationResponse>>() {});
        } else {
            handleErrorResponse(response);
            return null;
        }
    }

    // ۳. دریافت پیام‌های یک گفت‌وگو با conversationId از نوع String
    public List<MessageResponse> getMessages(String conversationId) throws Exception {
        String userId = SessionManager.getInstance().getUserId();
        if (userId == null) throw new Exception("کاربر وارد سیستم نشده است.");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/chats/conversations/" + conversationId + "/messages"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), new TypeReference<List<MessageResponse>>() {});
        } else {
            handleErrorResponse(response);
            return null;
        }
    }

    // ۴. ارسال پیام جدید در گفت‌وگو
    public void sendMessage(String adId, String content) throws Exception {
        String userId = SessionManager.getInstance().getUserId();
        if (userId == null) throw new Exception("کاربر وارد سیستم نشده است.");

        SendMessageRequest sendRequest = new SendMessageRequest(content);
        String jsonBody = objectMapper.writeValueAsString(sendRequest);

        String url = ApiConfig.BASE_URL + "/api/chats/send?advertisementId=" +
                URLEncoder.encode(adId, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 201) {
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
            throw new Exception("خطایی در بخش پیام‌ها رخ داد. کد وضعیت: " + response.statusCode());
        }
    }
}