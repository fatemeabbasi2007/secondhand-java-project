package org.example.frontend.controller;

import javafx.scene.control.*;
import org.example.frontend.service.AdvertisementService;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import org.example.frontend.util.NavigationService;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import java.nio.file.Files;
import java.util.Base64;

public class NewAdController {

    public Button submitButton;
    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private TextField priceField;
    @FXML private TextField cityField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextField imageUrlsField;
    // change format for picture
    @FXML private ImageView previewImageView;
    @FXML private Label imageStatusLabel;


    private final AdvertisementService adService = new AdvertisementService();

    // تعریف متغیر در بالای کلاس NewAdController برای نگهداری آی‌دی آگهی در حالت ویرایش
    private String editingAdId = null;

    @FXML
    public void initialize() {
        categoryComboBox.getItems().addAll("وسایل نقلیه", "خودرو", "موتور سیکلت", "املاک", "آپارتمان و مسکونی", "اداری و تجاری", "لوازم الکتریکی", "موبایل و تبلت", "لپ تاپ و کامپیوتر", "وسایل خانه و آشپزخانه", "مبلمان و لوازم چوبی", "وسایل شخصی", "پوشاک و کیف و کفش");
    }

    // این متد برای پر کردن فیلدها در حالت ویرایش از طرف صفحه قبل صدا زده می‌شود
    public void setAdDataForEdit(String adId, String title, double price, String city, String category, String description, String text) {
        this.editingAdId = adId;

        titleField.setText(title);
        priceField.setText(String.valueOf(price));
        cityField.setText(city);
        categoryComboBox.setValue(category);
        descriptionField.setText(description);
        imageUrlsField.setText(text);

        categoryComboBox.setDisable(true);
    }

    @FXML
    public void onSubmitAdClick(ActionEvent event) {
        resetFieldStyles();

        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        String priceStr = priceField.getText().trim();
        String city = cityField.getText().trim();
        String category = categoryComboBox.getValue() != null ? categoryComboBox.getValue().trim() : "";
        String urlsText = imageUrlsField.getText().trim();

        boolean hasError = false;

        // بررسی فیلدهای خالی و اعمال استایل قرمز
        if (title.isEmpty()) { titleField.setStyle("-fx-border-color: red; -fx-border-width: 1.5px;"); hasError = true; }
        if (description.isEmpty()) { descriptionField.setStyle("-fx-border-color: red; -fx-border-width: 1.5px;"); hasError = true; }
        if (city.isEmpty()) { cityField.setStyle("-fx-border-color: red; -fx-border-width: 1.5px;"); hasError = true; }
        if (category.isEmpty()) { categoryComboBox.setStyle("-fx-border-color: red; -fx-border-width: 1.5px;"); hasError = true; }
        if (urlsText.isEmpty()) { imageUrlsField.setStyle("-fx-border-color: red; -fx-border-width: 1.5px;"); hasError = true; }

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

        if (hasError) return; // توقف متد در صورت وجود خطا در فیلدها

        double pricee = Double.parseDouble(priceStr);

        //  تبدیل متن فیلد عکس‌ها (جدا شده با ویرگول) به List<String>

        java.util.List<String> imageUrlsList = new java.util.ArrayList<>();
        if (!urlsText.isEmpty()) {
            for (String url : urlsText.split(",")) {
                if (!url.trim().isEmpty()) {
                    imageUrlsList.add(url.trim());
                }
            }
        }

        try {
            // بخش اصلی
            if (editingAdId != null) {
                // اگر متغیر آی‌دی خالی نبود، یعنی در حال ویرایش هستیم:
                //  پاس دادن لیست عکس‌ها به متد ویرایش
                adService.updateAdvertisement(editingAdId, title, description, pricee, city, category, imageUrlsList);
                showAlert(Alert.AlertType.INFORMATION, "موفق", "تغییرات آگهی با موفقیت ذخیره شد.");
            } else {
                // اگر خالی بود، یعنی کاربر دارد آگهی جدید ثبت می‌کند:
                //  پاس دادن لیست عکس‌ها به متد ساخت آگهی جدید
                adService.createAdvertisement(title, description, pricee, city, category, imageUrlsList);
                showAlert(Alert.AlertType.INFORMATION, "موفق","آگهی با موفقیت ثبت شد و در انتظار بررسی مدیر قرار گرفت.");
            }

            clearFields(); //
            imageUrlsField.clear(); // ◄ ۴. پاک کردن فیلد عکس‌ها بعد از موفقیت
            editingAdId = null; // ریست کردن وضعیت به حالت عادی بعد از موفقیت

            // بازگشت خودکار به صفحه اصلی بعد از ثبت یا ویرایش
            NavigationService.navigate(event, "main-view.fxml", "صفحه اصلی آگهی‌ها");

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "خطا در عملیات", e.getMessage());
        }
    }




    // ۲. اکشن دکمه انتخاب عکس
    @FXML
    public void onSelectImageClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("انتخاب تصویر آگهی");

        // محدود کردن به فایل‌های تصویری
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.webp")
        );

        javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                // تبدیل عکس به رشته Base64
                String base64Image = convertFileToBase64(selectedFile);

                // قرار دادن رشته متنی در فیلد متنی جهت ارسال به بک‌اند
                imageUrlsField.setText(base64Image);

                // نمایش پیش‌نمایش در UI
                Image image = new Image(selectedFile.toURI().toString());
                previewImageView.setImage(image);

                if (imageStatusLabel != null) {
                    imageStatusLabel.setText("تصویر انتخاب شد: " + selectedFile.getName());
                }

            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "خطا در بارگذاری عکس", "تبدیل فایل عکس با خطا مواجه شد.");
            }
        }
    }

    // ۳. متد تبدیل فایل به Base64 Data URL
    private String convertFileToBase64(File file) throws Exception {
        byte[] fileContent = Files.readAllBytes(file.toPath());
        String base64String = Base64.getEncoder().encodeToString(fileContent);

        String mimeType = Files.probeContentType(file.toPath());
        if (mimeType == null) mimeType = "image/png";

        return "data:" + mimeType + ";base64," + base64String;
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
        categoryComboBox.setStyle("");
    }

    private void clearFields() {
        titleField.clear();
        descriptionField.clear();
        priceField.clear();
        cityField.clear();
        categoryComboBox.setValue(null);
        categoryComboBox.setDisable(false);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}