package org.example.frontend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.frontend.config.ApiConfig;
import org.example.frontend.model.*;
import org.example.frontend.security.SessionManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ChatService {

    private final HttpClient client;
    private final ObjectMapper objectMapper;

    public ChatService() {
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    // ارسال درخواست شروع گفت‌وگو یا فرستادن اولین پیام (مراحل ۴ تا ۹ سناریو)
    public void startConversation(Long adId, String messageText) throws Exception {
        String token = SessionManager.getInstance().getToken();
        if (token == null) {
            throw new Exception("جهت شروع گفت‌وگو ابتدا باید وارد حساب خود شوید.");
        }

        StartChatRequest chatRequest = new StartChatRequest(adId, messageText);
        String jsonBody = objectMapper.writeValueAsString(chatRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/conversations/start"))
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
                throw new Exception("خطا در ایجاد گفت‌وگو. کد وضعیت: " + response.statusCode());
            }
        }
    }

    public List<ConversationResponse> getConversations() throws Exception {
        String token = SessionManager.getInstance().getToken();
        if (token == null) throw new Exception("کاربر وارد سیستم نشده است.");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/conversations"))
                .header("Authorization", "Bearer " + token)
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

    // ۲. دریافت پیام‌های یک گفت‌وگو به ترتیب زمان (مراحل ۵ و ۶ سناریو)
    public List<MessageResponse> getMessages(Long conversationId) throws Exception {
        String token = SessionManager.getInstance().getToken();
        if (token == null) throw new Exception("کاربر وارد سیستم نشده است.");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/conversations/" + conversationId + "/messages"))
                .header("Authorization", "Bearer " + token)
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

    // ۳. ارسال پیام جدید در گفت‌وگو (مراحل ۷ و ۸ سناریو)
    public void sendMessage(Long conversationId, String content) throws Exception {
        String token = SessionManager.getInstance().getToken();
        if (token == null) throw new Exception("کاربر وارد سیستم نشده است.");

        SendMessageRequest sendRequest = new SendMessageRequest(content);
        String jsonBody = objectMapper.writeValueAsString(sendRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/conversations/" + conversationId + "/messages"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
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
