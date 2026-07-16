package org.example.frontend.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.frontend.network.NetworkService; // وارد کردن سرویس شبکه واقعی

import java.io.IOException;
import java.net.http.HttpResponse;

public class RegisterController {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel; // از همین لیبل هم برای خطا (قرمز) و هم موفقیت (سبز) استفاده می‌کنیم

    // تعریف سرویس شبکه برای ارتباط با سرور واقعی
    private final NetworkService networkService = new NetworkService();

    @FXML
    private void handleRegister() {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // ۱. بررسی خالی نبودن کادرها (مرحله ۳ سناریو)
        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showNotification("لطفاً تمامی کادرها را پر کنید!", "#FF4757");
            return;
        }

        // ۲. بررسی قالب شماره تلفن همراه (مرحله ۴ سناریو)
        if (!phone.matches("^09\\d{9}$")) {
            showNotification("شماره تلفن وارد شده معتبر نیست! باید با 09 شروع شده و ۱۱ رقم باشد.", "#FF4757");
            return;
        }

        try {
            // ۳. فرستادن اطلاعات ثبت‌نام به بک‌اَند واقعی با استفاده از NetworkService
            HttpResponse<String> response = networkService.register(fullName, email, phone, username, password);

            // ۴. بررسی پاسخ بک‌اَند
            if (response.statusCode() == 200 || response.statusCode() == 201) {
                // ثبت‌نام موفقیت‌آمیز بود (کد ۲۰۰ یا ۲۰۱)
                System.out.println("کاربر جدید در دیتابیس بک‌اَند ذخیره شد | نقش: USER | وضعیت: ACTIVE");

                showNotification("ثبت‌نام موفقیت‌آمیز بود! در حال انتقال به صفحه ورود...", "#2ED573");

                // ۲ ثانیه مکث برای دیدن پیام موفقیت و سپس انتقال به صفحه لاگین
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        javafx.application.Platform.runLater(this::handleGoToLogin);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();

            } else {
                // اگر سرور خطایی داد (مثلاً نام کاربری یا ایمیل تکراری بود)
                // بدنه پاسخ سرور (response.body) شامل پیغام خطایی است که بک‌اَند فرستاده است
                showNotification("خطا در ثبت‌نام: " + response.body(), "#FF4757");
            }

        } catch (Exception e) {
            // خطاهای مربوط به عدم برقراری ارتباط با سرور
            showNotification("ارتباط با سرور برقرار نشد: " + e.getMessage(), "#FF4757");
        }
    }

    @FXML
    private void handleGoToLogin() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/frontend/login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 350, 420);
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showNotification("خطا در بارگذاری صفحه ورود!", "#FF4757");
        }
    }

    // متد کمکی برای تنظیم رنگ و متن اعلان به کاربر
    private void showNotification(String message, String hexColor) {
        errorLabel.setStyle("-fx-text-fill: " + hexColor + ";");
        errorLabel.setText(message);
    }
}