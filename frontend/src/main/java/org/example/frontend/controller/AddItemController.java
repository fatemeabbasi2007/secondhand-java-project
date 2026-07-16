package org.example.frontend.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.frontend.network.NetworkService;
import org.example.frontend.utils.ErrorUtils;

import java.io.File;
import java.net.http.HttpResponse;

public class AddItemController {

    @FXML private TextField titleField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private ComboBox<String> cityComboBox;
    @FXML private TextField priceField;
    @FXML private TextArea descriptionField;
    @FXML private VBox extraFieldsContainer;
    @FXML private Label imagePathLabel;
    @FXML private Label statusLabel;

    private File selectedImageFile = null;
    private final NetworkService networkService = new NetworkService();

    @FXML
    public void initialize() {
        categoryComboBox.getItems().addAll("الکترونیک و دیجیتال", "وسایل نقلیه", "کتاب و لوازم تحریر", "پوشاک و کفش");
        cityComboBox.getItems().addAll("تهران", "اصفهان", "شیراز", "مشهد", "تبریز");
    }

    /**
     * مدیریت انتخاب تصویر کالا توسط کاربر
     */
    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("انتخاب تصویر کالا");
        // فیلتر کردن فرمت‌های تصویری مجاز
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        // گرفتن پنجره فعلی برای نمایش دیالوگ انتخاب فایل
        Stage stage = (Stage) statusLabel.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            selectedImageFile = file;
            imagePathLabel.setText("📂 تصویر انتخاب شد: " + file.getName());
        } else {
            imagePathLabel.setText("تصویری انتخاب نشده است");
        }
    }

    /**
     * ارسال اطلاعات آگهی به سرور
     */
    @FXML
    private void handleAddItem() {
        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        String priceText = priceField.getText().trim();

        // اعتبار سنجی ساده
        if (title.isEmpty() || priceText.isEmpty() || categoryComboBox.getValue() == null || cityComboBox.getValue() == null) {
            showError("لطفاً همه‌ی فیلدها را به درستی پر کنید!");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);

            // تبدیل دسته‌بندی و شهر به ID (بر اساس اندیس به همراه ۱)
            int categoryId = categoryComboBox.getSelectionModel().getSelectedIndex() + 1;
            int cityId = cityComboBox.getSelectionModel().getSelectedIndex() + 1;

            // ۲. ارسال واقعی به سرور
            HttpResponse<String> response = networkService.createAdvertisement(
                    title, description, price, categoryId, cityId
            );

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                statusLabel.setStyle("-fx-text-fill: #2ED573;");
                statusLabel.setText("آگهی با موفقیت ثبت شد! 🎉");
                clearForm();
            } else {
                // استفاده از کلاس کمکی متمرکز برای نشان دادن خطای دقیق بک‌اند
                String errorMsg = ErrorUtils.getErrorMessage(response);
                showError(errorMsg);
            }
        } catch (NumberFormatException e) {
            showError("قیمت وارد شده باید یک عدد معتبر باشد!");
        } catch (Exception e) {
            showError("خطا در برقراری ارتباط با سرور: " + e.getMessage());
        }
    }

    private void showError(String message) {
        statusLabel.setStyle("-fx-text-fill: #FF4757;");
        statusLabel.setText(message);
    }

    private void clearForm() {
        titleField.clear();
        categoryComboBox.getSelectionModel().clearSelection();
        cityComboBox.getSelectionModel().clearSelection();
        priceField.clear();
        descriptionField.clear();
        selectedImageFile = null;
        imagePathLabel.setText("هیچ تصویری انتخاب نشده است");
        if (extraFieldsContainer != null) {
            extraFieldsContainer.getChildren().clear();
        }
    }
}