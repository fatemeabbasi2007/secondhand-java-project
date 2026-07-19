package org.example.frontend.controller;

import javafx.scene.control.Button;
import org.example.frontend.service.AdvertisementService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import org.example.frontend.util.NavigationService;

public class NewAdController {

    public Button submitButton;
    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private TextField priceField;
    @FXML private TextField cityField;
    @FXML private TextField categoryField;

    private final AdvertisementService adService = new AdvertisementService();

    // تعریف متغیر در بالای کلاس NewAdController برای نگهداری آی‌دی آگهی در حالت ویرایش
    private Long editingAdId = null;

    // این متد برای پر کردن فیلدها در حالت ویرایش از طرف صفحه قبل صدا زده می‌شود
    public void setAdDataForEdit(Long adId, String title, double price, String city, String category, String description) {
        this.editingAdId = adId;

        titleField.setText(title);
        priceField.setText(String.valueOf(price));
        cityField.setText(city);
        categoryField.setText(category);
        descriptionField.setText(description);
    }

    @FXML
    public void onSubmitAdClick(ActionEvent event) {
        resetFieldStyles();

        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        String priceStr = priceField.getText().trim();
        String city = cityField.getText().trim();
        String category = categoryField.getText().trim();

        boolean hasError = false;

        // بررسی فیلدهای خالی و اعمال استایل قرمز
        if (title.isEmpty()) { titleField.setStyle("-fx-border-color: red; -fx-border-width: 1.5px;"); hasError = true; }
        if (description.isEmpty()) { descriptionField.setStyle("-fx-border-color: red; -fx-border-width: 1.5px;"); hasError = true; }
        if (city.isEmpty()) { cityField.setStyle("-fx-border-color: red; -fx-border-width: 1.5px;"); hasError = true; }
        if (category.isEmpty()) { categoryField.setStyle("-fx-border-color: red; -fx-border-width: 1.5px;"); hasError = true; }

        double price = 0;
        if (priceStr.isEmpty()) {
            priceField.setStyle("-fx-border-color: red; -fx-border-width: 1.5px;");
            hasError = true;
        } else {
            try {
                price = Double.parseDouble(priceStr);
                if (price < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                priceField.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
                showAlert(Alert.AlertType.WARNING, "خطای مقدار", "لطفاً قیمت را به صورت یک عدد معتبر و مثبت وارد کنید.");
                return;
            }
        }

        double pricee = Double.parseDouble(priceStr);

        try {
            // بخش اصلی :
            if (editingAdId != null) {
                // اگر متغیر آی‌دی خالی نبود، یعنی در حال ویرایش هستیم:
                adService.updateAdvertisement(editingAdId, title, description, pricee, city, category);
                showAlert(Alert.AlertType.INFORMATION, "موفق", "تغییرات آگهی با موفقیت ذخیره شد.");
            } else {
                // اگر خالی بود، یعنی کاربر دارد آگهی جدید ثبت می‌کند:
                adService.createAdvertisement(title, description, pricee, city, category);
                showAlert(Alert.AlertType.INFORMATION, "موفق", "آگهی با موفقیت ثبت شد و در انتظار بررسی مدیر قرار گرفت.");
            }

            clearFields();
            editingAdId = null; // ریست کردن وضعیت به حالت عادی بعد از موفقیت

            // بازگشت خودکار به صفحه اصلی بعد از ثبت یا ویرایش
            NavigationService.navigate(event, "main-view.fxml", "صفحه اصلی آگهی‌ها");

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "خطا در عملیات", e.getMessage());
        }
    }

    @FXML
    public void onCancelClick(ActionEvent event) {
        System.out.println("بازگشت به صفحه اصلی...");
        NavigationService.navigate(event, "main-view.fxml", "صفحه اصلی آگهی‌ها");
    }

    private void resetFieldStyles() {
        titleField.setStyle("");
        descriptionField.setStyle("");
        priceField.setStyle("");
        cityField.setStyle("");
        categoryField.setStyle("");
    }

    private void clearFields() {
        titleField.clear();
        descriptionField.clear();
        priceField.clear();
        cityField.clear();
        categoryField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}