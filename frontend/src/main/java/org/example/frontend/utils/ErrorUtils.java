package org.example.frontend.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.http.HttpResponse;

public class ErrorUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * تحلیل پاسخ سرور و برگرداندن یک پیام خطای فارسی و مناسب برای کاربر
     */
    public static String getErrorMessage(HttpResponse<String> response) {
        if (response == null) {
            return "پاسخی از سرور دریافت نشد!";
        }

        // ۱. تلاش برای استخراج فیلد message یا error از داخل بدنه JSON پاسخ سرور
        try {
            String body = response.body();
            if (body != null && !body.trim().isEmpty()) {
                JsonNode root = objectMapper.readTree(body);
                if (root.has("message")) {
                    return root.get("message").asText();
                } else if (root.has("error")) {
                    return root.get("error").asText();
                }
            }
        } catch (Exception e) {
            // بدنه پاسخ JSON نبوده یا ساختار متفاوتی داشته است؛ به سراغ کدهای وضعیت می‌رویم.
        }

        // ۲. نگاشت کدهای وضعیت اعلام‌شده در داکیومنت پروژه به پیام‌های فارسی مناسب
        switch (response.statusCode()) {
            case 400:
                return "اطلاعات ارسالی نامعتبر است. لطفاً ورودی‌ها را بررسی کنید.";
            case 401:
                return "شما وارد سیستم نشده‌اید یا مهلت اعتبار ورودتان تمام شده است.";
            case 403:
                return "شما اجازه دسترسی یا انجام این عملیات را ندارید.";
            case 404:
                return "داده یا آگهی مورد نظر یافت نشد!";
            case 500:
                return "خطای داخلی در سرور رخ داده است. لطفاً بعداً تلاش کنید.";
            default:
                return "خطای ناشناخته رخ داد. (کد وضعیت: " + response.statusCode() + ")";
        }
    }
}