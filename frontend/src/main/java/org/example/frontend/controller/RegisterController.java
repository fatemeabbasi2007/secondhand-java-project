package org.example.frontend.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class RegisterController {

    @FXML
    private TextField fullNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    // متغیر متصل به لیبل قرمز خطا روی صفحه ثبت‌نام
    @FXML
    private Label errorLabel;

    @FXML
    private void handleRegister() {
        String fullName = fullNameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        // ۱. بررسی اینکه هیچ‌کدام از فیلدها خالی نباشند
        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("لطفاً تمامی فیلدها را پر کنید!");
            return;
        }

        // ۲. پاک کردن متن خطا در صورت موفقیت‌آمیز بودن ورودی‌ها
        errorLabel.setText("");

        System.out.println("ثبت‌نام کاربر با موفقیت شبیه‌سازی شد:");
        System.out.println("نام: " + fullName + " | تلفن: " + phone);

        // ۳. هدایت کاربر به صفحه لاگین پس از ثبت‌نام موفق
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/frontend/login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 350, 420);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("خطا در بازگشت به صفحه ورود!");
        }
    }

    @FXML
    private void handleGoToLogin() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/frontend/login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 350, 420);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("خطا در بارگذاری صفحه ورود!");
        }
    }
}