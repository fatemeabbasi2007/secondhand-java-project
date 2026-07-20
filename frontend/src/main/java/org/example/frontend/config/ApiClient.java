package org.example.frontend.config;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.http.HttpClient;

public class ApiClient {
    // ۱. ساخت CookieManager برای ذخیره خودکار کوکی‌های JSESSIONID سشن
    private static final CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);

    // ۲. ساخت یک HttpClient سینگلتون (اشتراکی) برای کل برنامه
    private static final HttpClient client = HttpClient.newBuilder()
            .cookieHandler(cookieManager)
            .build();

    public static HttpClient getClient() {
        return client;
    }
}