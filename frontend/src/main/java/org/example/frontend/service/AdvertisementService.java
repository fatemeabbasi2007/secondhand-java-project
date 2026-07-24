package org.example.frontend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.frontend.config.ApiClient;
import org.example.frontend.config.ApiConfig;
import org.example.frontend.model.AdvertisementRequest;
import org.example.frontend.model.ErrorResponse;
import org.example.frontend.security.SessionManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class AdvertisementService {

    private final HttpClient client;
    private final ObjectMapper objectMapper;

    public AdvertisementService() {
        this.client = ApiClient.getClient();
        this.objectMapper = new ObjectMapper();
    }

    public void createAdvertisement(String title, String description, double price, String city, String category, List<String> imageUrls, Map<String,String>  attributesJson) throws Exception {
        String userId = SessionManager.getInstance().getUserId();
        if (userId == null) {
            throw new Exception("شما وارد حساب کاربری خود نشده‌اید.");
        }

        // ۱. آدرس ساده بدون پارامترهای اضافی در URL
        String url = ApiConfig.BASE_URL + "/api/advertisements/create";

        // ۲. ساخت درخواست JSON و تزریق لیست عکس‌ها درون بدنه
        AdvertisementRequest adRequest = new AdvertisementRequest(title, description, price, city, toCategoryId(category), attributesJson);
        adRequest.setImageUrlsList(imageUrls); // 👈 عکس‌ها داخل بدنه قرار می‌گیرند

        String jsonBody = objectMapper.writeValueAsString(adRequest);

        //System.out.println("==============");
       // System.out.println(jsonBody);
        //System.out.println("==============");
        // ۳. ارسال درخواست POST
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 201 && response.statusCode() != 200) {
            try {
                ErrorResponse error = objectMapper.readValue(response.body(), ErrorResponse.class);
                throw new Exception(error.getMessage());
            } catch (Exception exception) {
                throw new Exception("خطا در ثبت آگهی (کد " + response.statusCode() + "): " + response.body());
            }
        }
    }

    // ارسال درخواست ویرایش آگهی به بک‌اند با متد PUT
    public void updateAdvertisement(String adId, String title, String description, double price, String city, String category, List<String> imageUrls, Map<String , String> attributesJson) throws Exception {
        //  بررسی لاگین بودن کاربر از روی سشن
        String userId = SessionManager.getInstance().getUserId();
        if (userId == null) {
            throw new Exception("شما وارد حساب کاربری خود نشده‌اید.");
        }

        //  ساخت شیء درخواست به همراه آیدی آگهی
        AdvertisementRequest adRequest = new AdvertisementRequest(adId, title, description, price, city, toCategoryId(category), imageUrls, attributesJson);
        String jsonBody = objectMapper.writeValueAsString(adRequest);

        // ۳. ارسال درخواست PUT کاملاً سشن‌محور و بدون توکن هدر
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/api/advertisements/own/" + adId))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // ۴. بررسی موفقیت‌آمیز بودن پاسخ
        if (response.statusCode() != 200) {
            try {
                ErrorResponse error = objectMapper.readValue(response.body(), ErrorResponse.class);
                throw new Exception(error.getMessage());
            } catch (Exception e) {
                throw new Exception("خطا در ویرایش آگهی، کد وضعیت: " + response.statusCode());
            }
        }
    }

    private String toCategoryId (String category) {
        switch (category.trim()) {

            case "وسایل نقلیه":
                return "VEHICLES";
            case "خودرو":
                return "CARS";
            case "موتورسیکلت":
                return "MOTORCYCLES";

            case "املاک":
                return "REAL_ESTATE";
            case "آپارتمان و مسکونی":
                return "APARTMENTS";
            case "اداری و تجاری":
                return "COMMERCIAL";

            case "لوازم الکترونیکی":
                return "ELECTRONICS";
            case "موبایل و تبلت":
                return "MOBILE_PHONES";
            case "لپ‌تاپ و کامپیوتر":
                return "LAPTOPS";

            case "وسایل خانه و آشپزخانه":
                return "HOME_GOODS";
            case "مبلمان و لوازم چوبی":
                return "FURNITURE";

            case "وسایل شخصی":
                return "PERSONAL_ITEMS";
            case "پوشاک و کیف و کفش":
                return "CLOTHING";

            default:
                return "";
        }
    }
}