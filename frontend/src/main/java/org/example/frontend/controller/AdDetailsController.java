package org.example.frontend.controller;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.frontend.model.AdDetailsResponse;
import org.example.frontend.service.PublicAdService;
import org.example.frontend.security.SessionManager;
import org.example.frontend.util.NavigationService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.event.ActionEvent;

import java.util.stream.Collectors;

public class AdDetailsController {

    @FXML private Label titleLabel;
    @FXML private Label priceLabel;
    @FXML private Label cityLabel;
    @FXML private Label categoryLabel;
    @FXML private Label ownerLabel;
    @FXML private Label ratingLabel;
    @FXML private Label statusLabel;
    @FXML private TextArea descriptionArea;
    @FXML private Label imageLabel;
    @FXML private HBox buyerActionsBox;
    @FXML private HBox ownerActionsBox;
    @FXML private ImageView adImageView;
    @FXML private javafx.scene.layout.VBox customAttributesBox;

    private final PublicAdService adService = new PublicAdService();
    private String currentAdId;
    private final org.example.frontend.service.ChatService chatService = new org.example.frontend.service.ChatService();
    private final org.example.frontend.service.RatingService ratingService = new org.example.frontend.service.RatingService();
    private final org.example.frontend.service.FavoriteService favoriteService = new org.example.frontend.service.FavoriteService();

    // متدی برای مقداردهی اولیه آگهی از خارج از کنترلر
    public void setAdId(String adId) {
        this.currentAdId = adId;
        loadAdDetails();
    }

    private void loadAdDetails() {
        try {
            AdDetailsResponse ad = adService.getAdvertisementDetails(currentAdId);

            if (ad == null) {
                showAlert(Alert.AlertType.ERROR, "خطا", "اطلاعات آگهی یافت نشد.");
                return;
            }

            // ۱. عنوان
            if (titleLabel != null) {
                titleLabel.setText(ad.getTitle() != null ? ad.getTitle() : "-");
            }

            // ۲. قیمت
            if (priceLabel != null) {
                priceLabel.setText(String.format("%,.0f تومان", ad.getPrice()));
            }

            // ۳. شهر
            if (cityLabel != null) {
                cityLabel.setText(ad.getCity() != null ? ad.getCity() : "-");
            }

            // ۴. دسته‌بندی
            if (categoryLabel != null) {
                categoryLabel.setText(ad.getCategory() != null ? ad.getCategory() : "-");
            }

            // ۵. نام فروشنده
            if (ownerLabel != null) {
                ownerLabel.setText(ad.getOwnerUsername() != null ? ad.getOwnerUsername() : "نامشخص");
            }

            // ۶. امتیاز فروشنده (بررسی ایمن مقادیر null)
            if (ratingLabel != null) {
                Double rating = ad.getOwnerAverageRating();
                if (rating != null && rating > 0) {
                    ratingLabel.setText(String.format("(امتیاز: %.1f ★)", rating));
                } else {
                    ratingLabel.setText("(امتیاز: - ★)");
                }
            }

            // ۷. وضعیت آگهی
            if (statusLabel != null) {
                statusLabel.setText(ad.getStatus() != null ? ad.getStatus() : "فعال");
            }

            // ۸. توضیحات (اصلاح شد: مقادیر به TextArea اضافه می‌شوند)
            if (descriptionArea != null) {
                descriptionArea.setText(ad.getDescription() != null ? ad.getDescription() : "");
            }

            // ۹. آدرس تصاویر (بررسی نال بودن لیست قبل از stream)
            if (imageLabel != null) {
                if (ad.getImageUrlsList() != null && !ad.getImageUrlsList().isEmpty()) {
                    imageLabel.setText(String.join(" ,", ad.getImageUrlsList()));
                } else {
                    imageLabel.setText("بدون تصویر");
                }
            }

            // for pictures
            if (ad.getImageUrlsList() != null && !ad.getImageUrlsList().isEmpty()) {
                String imageSource = ad.getImageUrlsList().get(0);
                // ساخت Image از روی رشته Base64 یا URL عادی
                Image image = new Image(imageSource);
                adImageView.setImage(image);
            }

            // نمایش مشخصات اختصاصی (دینامیک) آگهی
            if (customAttributesBox != null) {
                customAttributesBox.getChildren().clear(); // پاک کردن مشخصات قبلی

                if (ad.getAttributesJson() != null && !ad.getAttributesJson().trim().isEmpty()) {
                    try {
                        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                        java.util.Map<String, String> attributes = mapper.readValue(
                                ad.getAttributesJson(),
                                new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<String, String>>() {}
                        );

                        for (java.util.Map.Entry<String, String> entry : attributes.entrySet()) {
                            HBox row = new HBox(10);
                            row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                            Label keyLabel = new Label(entry.getKey() + ":");
                            keyLabel.setStyle("-fx-font-weight: bold; -fx-min-width: 80px;");

                            Label valueLabel = new Label(entry.getValue());

                            row.getChildren().addAll(keyLabel, valueLabel);
                            customAttributesBox.getChildren().add(row);
                        }
                    } catch (Exception e) {
                        System.err.println("خطا در رندر مشخصات دینامیک: " + e.getMessage());
                    }
                }
            }

            // ۱۰. مقایسه مالکیت آگهی با کاربر فعلی جهت نمایش دکمه‌های مناسب
            String currentUserId = SessionManager.getInstance().getUserId();
            Object ownerIdObj = ad.getOwnerId();
            String ownerIdStr = (ownerIdObj != null) ? ownerIdObj.toString() : null;

            if (currentUserId != null && currentUserId.equals(ownerIdStr)) {
                // آگهی متعلق به خود کاربر است
                if (ownerActionsBox != null) ownerActionsBox.setVisible(true);
                if (buyerActionsBox != null) buyerActionsBox.setVisible(false);
            } else {
                // آگهی متعلق به دیگری است
                if (buyerActionsBox != null) buyerActionsBox.setVisible(true);
                if (ownerActionsBox != null) ownerActionsBox.setVisible(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "خطا در دریافت اطلاعات", e.getMessage());
        }
    }

    @FXML
    public void onAddToFavoritesClick(ActionEvent event) {
        if (currentAdId == null) return;

        try {
            favoriteService.addToFavorites(currentAdId);
            showAlert(Alert.AlertType.INFORMATION, "موفق", "این آگهی به لیست علاقه‌مندی‌های شما اضافه شد.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "خطا در ثبت علاقه‌مندی", e.getMessage());
        }
    }

    @FXML
    public void onStartChatClick(ActionEvent event) {
        if (currentAdId == null) return;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("ارسال پیام به فروشنده");
        dialog.setHeaderText("گفت‌وگو برای آگهی: " + (titleLabel != null ? titleLabel.getText() : ""));
        dialog.setContentText("متن اولین پیام:");

        dialog.showAndWait().ifPresent(messageText -> {
            String text = messageText.trim();
            if (text.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "متن خالی", "نمی‌توانید پیام خالی ارسال کنید.");
                return;
            }

            try {
                chatService.startConversation(currentAdId, text);
                NavigationService.navigate(event, "chat-view.fxml", "صندوق پیام‌ها");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "خطا در ارسال پیام", e.getMessage());
            }
        });
    }

    @FXML
    public void onEditAdClick(ActionEvent event) {
        if (currentAdId == null) return;

        try {
            AdDetailsResponse ad = adService.getAdvertisementDetails(currentAdId);
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/org/example/frontend/new-advertisement.fxml"));
            javafx.scene.Parent root = loader.load();

            NewAdController editController = loader.getController();

            editController.setAdDataForEdit(
                    currentAdId,
                    titleLabel != null ? titleLabel.getText() : "",
                    priceLabel != null ? Double.parseDouble(priceLabel.getText().replaceAll("[^0-9]", "")) : 0.0,
                    cityLabel != null ? cityLabel.getText() : "",
                    categoryLabel != null ? categoryLabel.getText() : "",
                    descriptionArea != null ? descriptionArea.getText() : "",
                    imageLabel != null ? imageLabel.getText() : "",
                    ad != null ? ad.getAttributesJson() : "{}"
            );

            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("ویرایش آگهی");
            stage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "خطا", "خطا در بارگذاری صفحه ویرایش آگهی.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "خطا", "خطا در بارگذاری صفحه ویرایش آگهی.");
        }
    }

    @FXML
    public void onDeleteAdClick(ActionEvent event) {
        if (currentAdId == null) return;

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("تأیید حذف");
        confirmAlert.setHeaderText("آیا از حذف این آگهی اطمینان دارید؟");
        confirmAlert.setContentText("این عملیات غیرقابل بازگشت است.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    adService.deleteAdvertisement(currentAdId);
                    showAlert(Alert.AlertType.INFORMATION, "موفق", "آگهی شما با موفقیت حذف شد.");
                    NavigationService.navigate(event, "main-view.fxml", "صفحه اصلی آگهی‌ها");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "خطا در حذف آگهی", e.getMessage());
                }
            }
        });
    }

    @FXML
    public void onRateSellerClick(ActionEvent event) {
        if (currentAdId == null) return;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("امتیازدهی به فروشنده");
        dialog.setHeaderText("ثبت امتیاز برای فروشنده آگهی: " + (titleLabel != null ? titleLabel.getText() : ""));

        ButtonType submitButtonType = new ButtonType("ثبت امتیاز", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        javafx.scene.layout.VBox contentBox = new javafx.scene.layout.VBox(10);
        contentBox.setStyle("-fx-padding: 15;");

        Label ratingInfoLabel = new Label("امتیاز شما از ۱ تا ۵ *:");
        ComboBox<Integer> ratingComboBox = new ComboBox<>();
        ratingComboBox.getItems().addAll(5, 4, 3, 2, 1);
        ratingComboBox.setValue(5);

        Label commentLabel = new Label("نظر متنی شما (اختیاری):");
        TextArea commentTextArea = new TextArea();
        commentTextArea.setPromptText("تجربه خرید یا ارتباط خود با فروشنده را بنویسید...");
        commentTextArea.setPrefRowCount(4);

        contentBox.getChildren().addAll(ratingInfoLabel, ratingComboBox, commentLabel, commentTextArea);
        dialog.getDialogPane().setContent(contentBox);

        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == submitButtonType) {
                int ratingValue = ratingComboBox.getValue();
                String commentValue = commentTextArea.getText().trim();

                try {
                    ratingService.rateSeller(currentAdId, ratingValue, commentValue);
                    showAlert(Alert.AlertType.INFORMATION, "موفق", "امتیاز شما با موفقیت ثبت شد. متشکریم!");
                    loadAdDetails();
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "خطا در ثبت امتیاز", e.getMessage());
                }
            }
        });
    }

    @FXML
    public void onMarkAsSoldClick(ActionEvent event) {
        if (currentAdId == null) return;

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("تأیید تغییر وضعیت");
        confirmAlert.setHeaderText("آیا از تغییر وضعیت این آگهی به «فروخته شده» اطمینان دارید؟");
        confirmAlert.setContentText("با این کار، آگهی دیگر در لیست عمومی نمایش داده نخواهد شد.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    adService.markAsSold(currentAdId);
                    showAlert(Alert.AlertType.INFORMATION, "موفقیت", "وضعیت آگهی با موفقیت به «فروخته شده» تغییر یافت.");
                    loadAdDetails();
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "خطا در تغییر وضعیت", e.getMessage());
                }
            }
        });
    }

    @FXML
    public void onBackClick(ActionEvent event) {
        NavigationService.navigate(event, "main-view.fxml", "صفحه اصلی آگهی‌ها");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}