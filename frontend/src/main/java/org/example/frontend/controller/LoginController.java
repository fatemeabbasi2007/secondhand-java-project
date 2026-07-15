package org.example.frontend.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    // متغیر متصل به لیبل قرمز روی صفحه
    @FXML
    private Label errorLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // ۱. بررسی پر بودن فیلدها و نمایش خطا در صورت نیاز
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("لطفاً نام کاربری و رمز عبور را وارد کنید!");
            return;
        }

        // ۲. پاک کردن پیام خطا در صورت درست بودن مقادیر
        errorLabel.setText("");
        System.out.println("تلاش برای ورود با نام کاربری: " + username);

        // ۳. انتقال به صفحه اصلی
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();

            // لود کردن فایل fxml صفحه اصلی
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/frontend/main-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);

            stage.setScene(scene);
            stage.centerOnScreen(); // بردن پنجره به وسط مانیتور
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("خطا در بارگذاری صفحه اصلی برنامه!");
        }
    }

    @FXML
    private void handleGoToRegister() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();

            // لود کردن فایل fxml صفحه ثبت‌نام
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/frontend/register-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 380, 540);

            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("خطا در بارگذاری صفحه ثبت‌نام!");
        }
    }
}