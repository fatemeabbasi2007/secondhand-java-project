package org.example.frontend.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.frontend.model.LoginResponse;
import org.example.frontend.network.NetworkService;
import org.example.frontend.network.SessionManager;
import org.example.frontend.utils.ErrorUtils;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    // تعریف شیء شبکه واقعی برای ارتباط با بک‌اَند
    private final NetworkService networkService = new NetworkService();

    /**
     * متد اصلی ورود به برنامه (متصل شده به بک‌اَند واقعی)
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // ۱. بررسی خالی نبودن کادرها
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("لطفاً نام کاربری و رمز عبور را وارد کنید!");
            errorLabel.setStyle("-fx-text-fill: #FF4757;");
            return;
        }

        try {
            LoginResponse loginResponse = networkService.login(username, password);

            SessionManager.setSession(
                    loginResponse.getToken(),
                    loginResponse.getRole(),
                    loginResponse.getUserId(),
                    loginResponse.getUsername()
            );

            System.out.println("ورود موفقیت‌آمیز بود! خوش آمدید: " + SessionManager.getUsername());
            navigateToMain();

        } catch (Exception e) {
            // ⭐ کدی که باید جایگزین کنی این است:
            // ۱. پیام استخراج‌شده از اکسپشن (که حاوی بدنه خطا یا کد وضعیت است) را به متد کمکی می‌دهیم
            String farsiFriendlyError = ErrorUtils.getErrorMessageFromString(e.getMessage());

            // ۲. پیام فارسی و مناسب را به کاربر نشان می‌دهیم
            errorLabel.setText(farsiFriendlyError);
            errorLabel.setStyle("-fx-text-fill: #FF4757;");
        }
    }

    /**
     * متد انتقال به صفحه اصلی بعد از ورود موفقیت‌آمیز
     */
    private void navigateToMain() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();

            // لود کردن فایل fxml صفحه اصلی
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/frontend/main-view.fxml"));

            // اندازه استاندارد برای صفحه اصلی
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);

            stage.setScene(scene);
            stage.setTitle("صفحه اصلی");
            stage.centerOnScreen(); // بردن پنجره به وسط مانیتور
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("خطا در بارگذاری صفحه اصلی برنامه!");
            errorLabel.setStyle("-fx-text-fill: #FF4757;");
        }
    }

    /**
     * متد انتقال به صفحه ثبت‌نام (اگر دکمه‌ای برای ثبت نام در صفحه لاگین داری)
     */
    @FXML
    private void handleGoToRegister() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();

            // لود کردن فایل fxml صفحه ثبت‌نام
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/frontend/register-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 380, 540);

            stage.setScene(scene);
            stage.setTitle("ثبت نام کاربر جدید");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("خطا در بارگذاری صفحه ثبت‌نام!");
            errorLabel.setStyle("-fx-text-fill: #FF4757;");
        }
    }
}