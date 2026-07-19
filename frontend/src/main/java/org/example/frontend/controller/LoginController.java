package org.example.frontend.controller;

import org.example.frontend.service.AuthService;
import org.example.frontend.model.LoginResponse;
import org.example.frontend.security.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import org.example.frontend.util.NavigationService;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    private final AuthService authService = new AuthService();

    @FXML
    public void onLoginButtonClick(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // اعتبارسنجی اولیه فرم در فرانت‌اند
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "خطای ورودی", "لطفاً نام کاربری و رمز عبور را وارد کنید.");
            return;
        }

        try {
            // ارسال درخواست به لایه سرویس و دریافت پاسخ موفق
            LoginResponse response = authService.login(username, password);

            // ذخیره توکن و مشخصات کاربر در SessionManager
            SessionManager.getInstance().createSession(
                    response.getToken(),
                    response.getUserId(),
                    response.getUsername(),
                    response.getRole()
            );

            showAlert(Alert.AlertType.INFORMATION, "خوش آمدید", "ورود موفقیت‌آمیز بود!");

            // در بخش ورود موفق بعد از زدن دکمه ورود:
            if ("ADMIN".equalsIgnoreCase(response.getRole())) {
                // تغییر صفحه به پنل مدیریت
                NavigationService.navigate(event, "admin-panel.fxml", "پنل مدیریت سامانه");
            } else {
                // تغییر صفحه به لیست آگهی‌ها (کاربر عادی)
                NavigationService.navigate(event, "main-view.fxml", "صفحه اصلی آگهی‌ها");
            }

        } catch (Exception e) {
            // نمایش دقیق پیام خطایی که سرور فرستاده است (مثلاً رمز عبور اشتباه است)
            showAlert(Alert.AlertType.ERROR, "خطا در ورود", "ورود ناموفق: " + e.getMessage());
        }
    }

    @FXML
    public void onGoToRegisterClick(ActionEvent event) {
        // تغییر صفحه به ثبت‌نام
        NavigationService.navigate(event, "register.fxml", "ثبت نام در سامانه");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}