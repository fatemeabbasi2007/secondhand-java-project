package org.example.frontend.controller;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
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
    @FXML private VBox dynamicFieldsContainer;
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
        categoryComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateDynamicFields(newValue);
        });
    }

    private TextField mileageField;   // کارکرد خودرو
    private TextField areaField;      // متراژ ملک
    private TextField markField; // برای وسایل برقی مثل وسایل خانه و ابزار الکترونیک
    private TextField colorField; // برای وسایل شخصی

    private void updateDynamicFields(String category) {
        if (dynamicFieldsContainer == null) return;

        // ۱. پاک کردن فیلدهای قبلی از UI
        dynamicFieldsContainer.getChildren().clear();

        // ۲. ریست کردن رفرنس‌ها
        mileageField = null;
        areaField = null;
        markField = null;
        colorField = null;

        if (category == null) return;

        // ۳. ساخت فیلدهای متناسب با دسته‌بندی
        switch (category) {
            case "خودرو":
            case "وسایل نقلیه":
            case "موتور سیکلت":
                mileageField = new TextField();
                mileageField.setPromptText("کارکرد (کیلومتر)");
                dynamicFieldsContainer.getChildren().addAll(
                        new Label("مشخصات وسیله نقلیه:"),
                        mileageField
                );
                break;

            case "آپارتمان و مسکونی":
            case "املاک":
            case "اداری و تجاری":
                areaField = new TextField();
                areaField.setPromptText("متراژ (مترمربع)");

                dynamicFieldsContainer.getChildren().addAll(
                        new Label("مشخصات ملک:"),
                        areaField

                );
                break;

            case "لوازم الکترونیکی":
            case "موبایل و تبلت":
            case "لپ‌تاپ و کامپیوتر":
            case "وسایل خانه و آشپزخانه":
            case "مبلمان و لوازم چوبی":
                markField = new TextField();
                markField.setPromptText("مدل و برند");

                dynamicFieldsContainer.getChildren().addAll(
                        new Label("مشخصات جنس:"),
                        markField

                );
                break;

            case "وسایل شخصی":
            case "پوشاک و کیف و کفش":
                colorField = new TextField();
                colorField.setPromptText("رنگ");

                dynamicFieldsContainer.getChildren().addAll(
                        new Label("مشخصات وسایل:"),
                        colorField

                );
                break;

            default:
                // بقیه دسته‌بندی‌ها فیلد اضافه ندارند
                break;
        }
    }

    // این متد برای پر کردن فیلدها در حالت ویرایش از طرف صفحه قبل صدا زده می‌شود
    public void setAdDataForEdit(String adId, String title, double price, String city, String category, String description, String text, String attributesJson) {
        this.editingAdId = adId;

        titleField.setText(title);
        priceField.setText(String.valueOf(price));
        cityField.setText(city);
        categoryComboBox.setValue(category);
        descriptionField.setText(description);
        imageUrlsField.setText(text);

        updateDynamicFields(category);

        // ۲. پر کردن مقادیر قبلی فیلدها از روی رشته JSON
        if (attributesJson != null && !attributesJson.trim().isEmpty()) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                java.util.Map<String, String> attributes = mapper.readValue(
                        attributesJson,
                        new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<String, String>>() {}
                );

                // ست کردن مقادیر در فیلدها
                if (mileageField != null && attributes.containsKey("کارکرد")) {
                    // حذف عبارت "کیلومتر" برای قرارگیری عدد خالص در TextField
                    String mileageVal = attributes.get("کارکرد").replace("کیلومتر", "").trim();
                    mileageField.setText(mileageVal);
                }
                if (areaField != null && attributes.containsKey("متراژ")) {
                    String areaVal = attributes.get("متراژ").replace("متر مربع", "").trim();
                    areaField.setText(areaVal);
                }
                if (markField != null && attributes.containsKey("مدل و برند")) {
                    markField.setText(attributes.get("مدل و برند"));
                }
                if (colorField != null && attributes.containsKey("رنگ")) {
                    colorField.setText(attributes.get("رنگ"));
                }

            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "خطا در عملیات", e.getMessage());
            }
        }

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
            for (String url : urlsText.split("\\|")) {
                if (!url.trim().isEmpty()) {
                    imageUrlsList.add(url.trim());
                }
            }
        }

        String attributesJson = buildAttributesJson();

        try {
            // بخش اصلی
            if (editingAdId != null) {
                // اگر متغیر آی‌دی خالی نبود، یعنی در حال ویرایش هستیم:
                //  پاس دادن لیست عکس‌ها به متد ویرایش
                adService.updateAdvertisement(editingAdId, title, description, pricee, city, category, imageUrlsList, attributesJson);
                showAlert(Alert.AlertType.INFORMATION, "موفق", "تغییرات آگهی با موفقیت ذخیره شد.");
            } else {
                // اگر خالی بود، یعنی کاربر دارد آگهی جدید ثبت می‌کند:
                //  پاس دادن لیست عکس‌ها به متد ساخت آگهی جدید
                adService.createAdvertisement(title, description, pricee, city, category, imageUrlsList, attributesJson);
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


    private String buildAttributesJson() {
        java.util.Map<String, String> attributes = new java.util.HashMap<>();

        if (mileageField != null && !mileageField.getText().trim().isEmpty()) {
            attributes.put("کارکرد", mileageField.getText().trim() + " کیلومتر");
        }
        if (areaField != null && !areaField.getText().trim().isEmpty()) {
            attributes.put("متراژ", areaField.getText().trim() + " متر مربع");
        }
        if (markField != null && !markField.getText().trim().isEmpty()) {
            attributes.put("مدل و برند", markField.getText().trim());
        }
        if (colorField != null && !colorField.getText().trim().isEmpty()) {
            attributes.put("رنگ", colorField.getText().trim());
        }

        if (attributes.isEmpty()) {
            return "{}";
        }

        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.writeValueAsString(attributes);
        } catch (Exception e) {
            return "{}";
        }
    }




    // ۲. اکشن دکمه انتخاب عکس
    @FXML
    public void onSelectImageClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("انتخاب تصاویر آگهی");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.webp")
        );

        javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

        // ◄ ۱. تغییر به showOpenMultipleDialog برای انتخاب چندتایی
        java.util.List<File> selectedFiles = fileChooser.showOpenMultipleDialog(stage);

        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            try {
                java.util.List<String> base64List = new java.util.ArrayList<>();

                // اگر از قبل عکسی در فیلد بود، حفظش می‌کنیم
                String currentText = imageUrlsField.getText().trim();
                if (!currentText.isEmpty()) {
                    base64List.add(currentText);
                }

                // ۲. تبدیل همه فایل‌های انتخاب‌شده به Base64
                for (File file : selectedFiles) {
                    base64List.add(convertFileToBase64(file));
                }

                // ۳. چسباندن رشته‌ها به هم با علامت ویرگول (,)
                String combinedUrls = String.join("|", base64List);
                imageUrlsField.setText(combinedUrls);

                // ۴. نمایش پیش‌نمایش اولین عکس انتخاب‌شده
                Image firstImage = new Image(selectedFiles.get(0).toURI().toString());
                previewImageView.setImage(firstImage);

                if (imageStatusLabel != null) {
                    imageStatusLabel.setText(selectedFiles.size() + " تصویر انتخاب شد.");
                }

            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "خطا در بارگذاری عکس", "تبدیل فایل‌ها با خطا مواجه شد.");
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

        if (dynamicFieldsContainer != null) {
            dynamicFieldsContainer.getChildren().clear();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}