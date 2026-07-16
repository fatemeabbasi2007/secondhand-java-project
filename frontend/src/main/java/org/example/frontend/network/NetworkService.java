package org.example.frontend.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.frontend.model.LoginRequest;
import org.example.frontend.model.LoginResponse;
import org.example.frontend.model.RegisterRequest;
import org.example.frontend.model.Advertisement;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.example.frontend.model.ChatSession;
import org.example.frontend.model.ChatMessage;




public class NetworkService {

    // آدرس پایه API بک‌اندمان
    private static final String BASE_URL = "http://localhost:8080/api";

    private final HttpClient httpClient;

    // جکسون برای تبدیل خودکار کلاس‌های جاوا به متن JSON
    private final ObjectMapper objectMapper;

    public NetworkService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * متد ثبت آگهی جدید با هدر احراز هویت JWT
     */
    public HttpResponse<String> createAdvertisement(String title, String description, double price, int categoryId, int cityId) throws Exception {
        String url = BASE_URL + "/advertisements";

        // ساخت یک نقشه (Map) از مقادیر
        java.util.Map<String, Object> requestBody = java.util.Map.of(
                "title", title,
                "description", description,
                "price", price,
                "categoryId", categoryId,
                "cityId", cityId
        );

        // جکسون خودش به صورت خودکار Map را به JSON استاندارد تبدیل می‌کند (حتی کاراکترهای خاص را Escape می‌کند)
        String jsonBody = objectMapper.writeValueAsString(requestBody);

        // گرفتن توکن از SessionManager
        String token = SessionManager.getToken();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token) //
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * متد ورود به حساب کاربری با استفاده از مدل‌های جکسون
     */
    public LoginResponse login(String username, String password) throws Exception {
        String url = BASE_URL + "/auth/login";

        // ۱. استفاده از کلاس LoginRequest
        LoginRequest loginRequest = new LoginRequest(username, password);

        // ۲. تبدیل خودکار شیء به JSON توسط جکسون
        String jsonBody = objectMapper.writeValueAsString(loginRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            // ۳. تبدیل خودکار پاسخ JSON سرور به شیء LoginResponse
            return objectMapper.readValue(response.body(), LoginResponse.class);
        } else {
            String errorContent = response.body();
            if (errorContent != null && !errorContent.trim().isEmpty()) {
                throw new RuntimeException(errorContent);
            } else {
                throw new RuntimeException("HTTP_ERROR: " + response.statusCode());
            }
        }
    }

    /**
     * ۲. متد ارسال درخواست ثبت‌نام (Register) به بک‌اند
     */
    public HttpResponse<String> register(String fullName, String email, String phone, String username, String password) throws Exception {
        String url = BASE_URL + "/auth/register";

        // ساخت شیء درخواست ثبت‌نام (بدون دردسر فرمت دستی JSON)
        RegisterRequest registerRequest = new RegisterRequest(fullName, email, phone, username, password);

        // تبدیل خودکار کل اطلاعات ثبت‌نام به JSON توسط جکسون
        String jsonBody = objectMapper.writeValueAsString(registerRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * ۱. دریافت آگهی‌های منتظر تایید (PENDING) از سرور
     */
    public List<Advertisement> getPendingAdvertisements() throws Exception {
        String url = BASE_URL + "/advertisements/pending";
        String token = SessionManager.getToken();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), new TypeReference<List<Advertisement>>() {});
    }

    /**
     * ۲. تایید آگهی روی سرور توسط ادمین
     */
    public HttpResponse<String> approveAdvertisement(int adId) throws Exception {
        String url = BASE_URL + "/admin/advertisements/" + adId + "/approve";
        String token = SessionManager.getToken();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * ۳. رد آگهی روی سرور با ارسال علت رد به صورت JSON
     */
    public HttpResponse<String> rejectAdvertisement(int adId, String reason) throws Exception {
        String url = BASE_URL + "/admin/advertisements/" + adId + "/reject";
        String token = SessionManager.getToken();

        // تبدیل دلیل رد به فرمت JSON ساده
        String jsonBody = "{\"reason\":\"" + reason + "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * ۱. دریافت لیست تمام چت‌های کاربر فعلی از سرور
     */
    public List<ChatSession> getUserChats() throws Exception {
        String url = BASE_URL + "/chats";
        String token = SessionManager.getToken(); // دریافت توکن کاربر لاگین شده

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            // تبدیل جی‌سان سرور به لیستی از شیء‌های ChatSession
            return objectMapper.readValue(response.body(), new TypeReference<List<ChatSession>>() {});
        } else {
            throw new RuntimeException("خطا در دریافت لیست چت‌ها از سرور");
        }
    }

    /**
     * ۲. ارسال پیام جدید به یک چت خاص در سرور
     */
    public ChatMessage sendMessage(int chatId, String text) throws Exception {
        String url = BASE_URL + "/chats/" + chatId + "/messages";
        String token = SessionManager.getToken();

        // ساخت بدنه پیام به صورت ساختار ساده کلید-مقدار و تبدیل خودکار به JSON با جکسون
        java.util.Map<String, String> body = java.util.Map.of("text", text);
        String jsonBody = objectMapper.writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            // بازگرداندن پیام ثبت‌شده در سرور (شامل آیدی، متن و تاریخ ثبت شده در بک‌اند)
            return objectMapper.readValue(response.body(), ChatMessage.class);
        } else {
            throw new RuntimeException("سرور پیام شما را دریافت نکرد!");
        }
    }


    /**
     * ۱. شروع چت جدید یا گرفتن چت موجود برای یک آگهی خاص
     */
    public ChatSession startOrGetChat(int adId, String initialMessage) throws Exception {
        String url = BASE_URL + "/chats/start";
        String token = SessionManager.getToken(); // دریافت توکن معتبر

        // ساخت بدنه درخواست چت
        java.util.Map<String, Object> body = java.util.Map.of(
                "adId", adId,
                "messageText", initialMessage
        );
        String jsonBody = objectMapper.writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            // تبدیل پاسخ جی‌سان سرور به مدل واقعی ChatSession
            return objectMapper.readValue(response.body(), ChatSession.class);
        } else {
            throw new RuntimeException("خطا در شروع گفتگو: " + response.body());
        }
    }

    /**
     * ۲. ثبت امتیاز و نظر برای یک آگهی (ثبت فیدبک)
     */
    public HttpResponse<String> submitReview(int adId, int score, String comment) throws Exception {
        String url = BASE_URL + "/advertisements/" + adId + "/reviews";
        String token = SessionManager.getToken();

        java.util.Map<String, Object> body = java.util.Map.of(
                "score", score,
                "comment", comment
        );
        String jsonBody = objectMapper.writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * ۳. تغییر وضعیت آگهی به «فروخته شده» توسط مالک آگهی
     */
    public HttpResponse<String> markAdAsSold(int adId) throws Exception {
        String url = BASE_URL + "/advertisements/" + adId + "/sold";
        String token = SessionManager.getToken();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .PUT(HttpRequest.BodyPublishers.noBody()) // معمولاً متد آپدیت وضعیت بدنه نمی‌خواهد
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * ۴. حذف آگهی توسط مالک آگهی
     */
    public HttpResponse<String> deleteAdvertisement(int adId) throws Exception {
        String url = BASE_URL + "/advertisements/" + adId;
        String token = SessionManager.getToken();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .DELETE()
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * دریافت لیست تمامی آگهی‌های فعال و تایید شده از سرور
     */
    public List<Advertisement> getAllAdvertisements() throws Exception {
        String url = BASE_URL + "/advertisements";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), new TypeReference<List<Advertisement>>() {});
    }

    /**
     * جستجو و فیلتر پیشرفته آگهی‌ها بر روی سرور
     */
    public List<Advertisement> searchAdvertisements(String keyword, String category, String city, Double minPrice, Double maxPrice) throws Exception {
        // ساخت آدرس با پارامترهای جستجو (Query Parameters)
        StringBuilder urlBuilder = new StringBuilder(BASE_URL).append("/advertisements/search?");
        if (keyword != null && !keyword.isEmpty()) urlBuilder.append("keyword=").append(java.net.URLEncoder.encode(keyword, "UTF-8")).append("&");
        if (category != null && !"همه دسته‌بندی‌ها".equals(category)) urlBuilder.append("category=").append(java.net.URLEncoder.encode(category, "UTF-8")).append("&");
        if (city != null && !"همه شهرها".equals(city)) urlBuilder.append("city=").append(java.net.URLEncoder.encode(city, "UTF-8")).append("&");
        if (minPrice != null) urlBuilder.append("minPrice=").append(minPrice).append("&");
        if (maxPrice != null) urlBuilder.append("maxPrice=").append(maxPrice).append("&");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBuilder.toString()))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), new TypeReference<List<Advertisement>>() {});
    }




}