package org.example.frontend.controller;

import org.example.frontend.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import org.example.frontend.util.NavigationService;

public class RegisterController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField emailField;

    private final AuthService authService = new AuthService();

    @FXML
    public void onRegisterButtonClick(ActionEvent event) {
        String name = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        // اعتبارسنجی اولیه فرم (خالی نبودن فیلدهای ضروری) [cite: 927, 1131]
        if (name.isEmpty() || username.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "خطای اعتبارسنجی", "لطفاً تمام فیلدهای ضروری را پر کنید.");
            return;
        }

        try {
            // ارسال به لایه سرویس [cite: 928, 1151]
            authService.register(name, username, password, phone, email);

            // نمایش پیام موفقیت دریافتی [cite: 933, 1191]
            showAlert(Alert.AlertType.INFORMATION, "موفق", "ثبت‌نام با موفقیت انجام شد. حالا می‌توانید وارد شوید.");
            clearFields();

        } catch (Exception e) {
            // نمایش دقیق پیام خطای صادر شده از سمت بک‌اند [cite: 934, 1191]
            showAlert(Alert.AlertType.ERROR, "خطا در ثبت‌نام", e.getMessage());
        }
    }

    @FXML
    public void onBackToLoginClick(ActionEvent event) {
        // جابه‌جایی به صفحه ورود با استفاده از کلاس ناوبری
        NavigationService.navigate(event, "login.fxml", "ورود به سامانه");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        nameField.clear();
        usernameField.clear();
        passwordField.clear();
        phoneField.clear();
        emailField.clear();
    }


}