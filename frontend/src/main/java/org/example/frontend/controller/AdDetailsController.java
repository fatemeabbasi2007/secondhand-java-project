package org.example.frontend.controller;

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

    private final PublicAdService adService = new PublicAdService();
    private String currentAdId;
    // اضافه کردن فیلد سرویس چت به فیلدهای کلاس کنترلر
    private final org.example.frontend.service.ChatService chatService = new org.example.frontend.service.ChatService();
    // اضافه کردن فیلد سرویس امتیازدهی به فیلدهای کلاس کنترلر
    private final org.example.frontend.service.RatingService ratingService = new org.example.frontend.service.RatingService();
    // اضافه کردن سرویس به کنترلر
    private final org.example.frontend.service.FavoriteService favoriteService = new org.example.frontend.service.FavoriteService();

    // متدی برای مقداردهی اولیه آگهی از خارج از کنترلر (مثلاً از صفحه اصلی)
    public void setAdId(String adId) {
        this.currentAdId = adId;
        loadAdDetails();
    }

    private void loadAdDetails() {
        try {
            AdDetailsResponse ad = adService.getAdvertisementDetails(currentAdId);

            // نمایش فیلدها
            titleLabel.setText(ad.getTitle());
            priceLabel.setText(String.format("%,.0f تومان", ad.getPrice()));
            cityLabel.setText(ad.getCity());
            categoryLabel.setText(ad.getCategory());
            ownerLabel.setText(ad.getOwnerUsername());
            ratingLabel.setText(String.format("(امتیاز: %.1f ★)", ad.getOwnerAverageRating()));
            statusLabel.setText(ad.getStatus());
            imageLabel.setText(ad.getImageUrlsList().stream().collect(Collectors.joining(" ,")));

            // مقایسه مالکیت آگهی با کاربر فعلی
            String currentUserId = SessionManager.getInstance().getUserId();

            if (currentUserId != null && currentUserId.equals(ad.getOwnerId())) {
                // آگهی متعلق به خود کاربر است
                ownerActionsBox.setVisible(true);
                buyerActionsBox.setVisible(false);
            } else {
                // آگهی متعلق به دیگری است
                buyerActionsBox.setVisible(true);
                ownerActionsBox.setVisible(false);
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "خطا در دریافت اطلاعات", e.getMessage());
        }
    }



    @FXML
    public void onAddToFavoritesClick(ActionEvent event) {
        if (currentAdId == null) return;

        try {
            // ارسال درخواست افزودن به بک‌اند
            favoriteService.addToFavorites(currentAdId);
            showAlert(Alert.AlertType.INFORMATION, "موفق", "این آگهی به لیست علاقه‌مندی‌های شما اضافه شد.");
        } catch (Exception e) {
            // نمایش خطاهای بک‌اند
            showAlert(Alert.AlertType.ERROR, "خطا در ثبت علاقه‌مندی", e.getMessage());
        }
    }



    @FXML
    public void onStartChatClick(ActionEvent event) {
        if (currentAdId == null) return;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("ارسال پیام به فروشنده");
        dialog.setHeaderText("گفت‌وگو برای آگهی: " + titleLabel.getText());
        dialog.setContentText("متن اولین پیام:");

        dialog.showAndWait().ifPresent(messageText -> {
            String text = messageText.trim();
            if (text.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "متن خالی", "نمی‌توانید پیام خالی ارسال کنید.");
                return;
            }

            try {
                // ۱. ارسال پیام به سرور
                chatService.startConversation(currentAdId, text);

                // ۲. انتقال فوری کاربر به صفحه چت‌ها (صندوق ورودی) جهت ادامه گفت‌وگو
                System.out.println("انتقال به صندوق پیام‌ها پس از شروع موفق چت...");
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
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/org/example/frontend/new-ad.fxml"));
            javafx.scene.Parent root = loader.load();

            // دریافت کنترلر صفحه ثبت آگهی
            NewAdController editController = loader.getController();

            // فرستادن اطلاعات آگهی فعلی به فرم جهت ویرایش
            editController.setAdDataForEdit(
                    currentAdId,
                    titleLabel.getText(),
                    Double.parseDouble(priceLabel.getText().replaceAll("[^0-9]", "")), // استخراج فقط عدد از متن قیمت
                    cityLabel.getText(),
                    categoryLabel.getText(),
                    descriptionArea.getText(),
                    imageLabel.getText()
            );

            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("ویرایش آگهی");
            stage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "خطا", "خطا در بارگذاری صفحه ویرایش آگهی.");
        }
    }

    @FXML
    public void onDeleteAdClick(ActionEvent event) {
        if (currentAdId == null) return;

        // نمایش آلرت تأیید برای جلوگیری از حذف تصادفی
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("تأیید حذف");
        confirmAlert.setHeaderText("آیا از حذف این آگهی اطمینان دارید؟");
        confirmAlert.setContentText("این عملیات غیرقابل بازگشت است.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // ارسال درخواست حذف به سرویس و بک‌اند
                    adService.deleteAdvertisement(currentAdId);

                    showAlert(Alert.AlertType.INFORMATION, "موفق", "آگهی شما با موفقیت حذف شد.");

                    // بازگشت به صفحه اصلی آگهی‌ها
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

        // ۱. ساخت پنجره دیالوگ سفارشی برای امتیازدهی
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("امتیازدهی به فروشنده");
        dialog.setHeaderText("ثبت امتیاز برای فروشنده آگهی: " + titleLabel.getText());

        // ساخت دکمه‌های تایید و انصراف
        ButtonType submitButtonType = new ButtonType("ثبت امتیاز", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        // طراحی لایه داخلی پنجره دیالوگ
        javafx.scene.layout.VBox contentBox = new javafx.scene.layout.VBox(10);
        contentBox.setStyle("-fx-padding: 15;");

        Label ratingInfoLabel = new Label("امتیاز شما از ۱ تا ۵ *:");
        ComboBox<Integer> ratingComboBox = new ComboBox<>();
        ratingComboBox.getItems().addAll(5, 4, 3, 2, 1); // گزینه‌های معتبر امتیاز
        ratingComboBox.setValue(5); // پیش‌فرض ۵ ستاره

        Label commentLabel = new Label("نظر متنی شما (اختیاری):");
        TextArea commentTextArea = new TextArea();
        commentTextArea.setPromptText("تجربه خرید یا ارتباط خود با فروشنده را بنویسید...");
        commentTextArea.setPrefRowCount(4);

        contentBox.getChildren().addAll(ratingInfoLabel, ratingComboBox, commentLabel, commentTextArea);
        dialog.getDialogPane().setContent(contentBox);

        // منتظر ماندن برای تایید مدیر/کاربر
        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == submitButtonType) {
                int ratingValue = ratingComboBox.getValue();
                String commentValue = commentTextArea.getText().trim();

                try {
                    // ارسال درخواست به سرور
                    ratingService.rateSeller(currentAdId, ratingValue, commentValue);

                    showAlert(Alert.AlertType.INFORMATION, "موفق", "امتیاز شما با موفقیت ثبت شد. متشکریم!");

                    // بروزرسانی پویای اطلاعات آگهی برای نمایش سریع میانگین امتیاز جدید
                    loadAdDetails();

                } catch (Exception e) {
                    // نمایش خطای دقیق بک‌اند (مثلاً امتیازدهی تکراری یا تلاش برای امتیازدهی به خود)
                    showAlert(Alert.AlertType.ERROR, "خطا در ثبت امتیاز", e.getMessage());
                }
            }
        });
    }

    @FXML
    public void onMarkAsSoldClick(ActionEvent event) {
        if (currentAdId == null) return;

        // نمایش پیام تایید قبل از تغییر وضعیت
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("تأیید تغییر وضعیت");
        confirmAlert.setHeaderText("آیا از تغییر وضعیت این آگهی به «فروخته شده» اطمینان دارید؟");
        confirmAlert.setContentText("با این کار، آگهی دیگر در لیست عمومی نمایش داده نخواهد شد.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // ۱. فرستادن درخواست موازی/همگام به سرور
                    adService.markAsSold(currentAdId);

                    showAlert(Alert.AlertType.INFORMATION, "موفقیت", "وضعیت آگهی با موفقیت به «فروخته شده» تغییر یافت.");

                    // ۲. به‌روزرسانی پویای صفحه برای نمایش وضعیت جدید
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