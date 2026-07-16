package org.example.frontend.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.frontend.model.Advertisement;
import org.example.frontend.network.NetworkService;
import org.example.frontend.network.SessionManager;
import org.example.frontend.model.ChatSession;

import java.net.http.HttpResponse;

public class ProductDetailsController {

    @FXML private Label titleLabel;
    @FXML private Label priceLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label categoryLabel;
    @FXML private Label cityLabel;
    @FXML private Label sellerNameLabel;
    @FXML private Label sellerRatingLabel;
    @FXML private Label soldStatusLabel;
    @FXML private Label statusLabel;

    // فیلدهای امتیازدهی
    @FXML private TextField ratingField;
    @FXML private TextArea commentField;
    @FXML private Button btnSubmitReview;
    @FXML private VBox reviewSectionContainer;

    // کانتینرهای دکمه‌ها
    @FXML private HBox buyerActionsContainer;
    @FXML private HBox ownerActionsContainer;

    // دکمه‌های خاص
    @FXML private Button btnFavorite;
    @FXML private Button btnStartChat;
    @FXML private Button btnMarkAsSold;

    private MainController mainController;
    private Advertisement advertisement;
    private boolean isFavorite = false;

    // تعریف سرویس شبکه واقعی برای ارتباط با بک‌اَند
    private final NetworkService networkService = new NetworkService();

    public void setProductData(Advertisement ad, MainController mainController) {
        this.advertisement = ad;
        this.mainController = mainController;

        // پر کردن فیلدهای متنی آگهی
        this.titleLabel.setText(ad.getTitle());
        this.priceLabel.setText(String.format("%,.0f تومان", ad.getPrice()));
        this.descriptionLabel.setText(ad.getDescription());
        this.categoryLabel.setText("دسته‌بندی: " + ad.getCategoryName());
        this.cityLabel.setText("شهر: " + ad.getCityName());

        // پر کردن فیلدهای فروشنده
        this.sellerNameLabel.setText("نام فروشنده: " + ad.getSellerName());
        this.sellerRatingLabel.setText(String.format("امتیاز فروشنده: ⭐ %.1f از ۵", ad.getSellerRating()));

        // بررسی وضعیت فروخته شده بودن
        updateSoldUI();

        // بررسی اینکه آیا کاربر فعلی صاحب آگهی است یا خیر
        checkPermissions();
    }

    /**
     * سوئیچ کردن امکانات صفحه بر اساس مالک آگهی یا خریدار بودن
     */
    private void checkPermissions() {
        int currentUserId = SessionManager.getUserId(); //

        if (currentUserId == advertisement.getSellerId()) { //
            // اگر آگهی متعلق به خود کاربر باشد:
            buyerActionsContainer.setVisible(false);
            buyerActionsContainer.setManaged(false);
            reviewSectionContainer.setVisible(false);
            reviewSectionContainer.setManaged(false);

            ownerActionsContainer.setVisible(true);
            ownerActionsContainer.setManaged(true);
        } else {
            // اگر کاربر خریدار باشد:
            buyerActionsContainer.setVisible(true);
            buyerActionsContainer.setManaged(true);
            reviewSectionContainer.setVisible(true);
            reviewSectionContainer.setManaged(true);

            ownerActionsContainer.setVisible(false);
            ownerActionsContainer.setManaged(false);
        }
    }

    private void updateSoldUI() {
        if (advertisement.isSold()) { //
            soldStatusLabel.setVisible(true);
            btnMarkAsSold.setText("🤝 فروخته شده");
            btnMarkAsSold.setDisable(true);
            btnStartChat.setDisable(true);
        } else {
            soldStatusLabel.setVisible(false);
        }
    }

    @FXML
    private void handleBack() {
        mainController.loadProducts(); //
    }

    // ================= دکمه‌های مخصوص خریدار =================

    /**
     * ارسال اولین پیام و باز کردن واقعی چت روی سرور
     */
    @FXML
    private void handleStartChat() {
        int currentUserId = SessionManager.getUserId(); //

        // بررسی اینکه خریدار خودش مالک نباشد
        if (currentUserId == advertisement.getSellerId()) { //
            showAlert("خطا", "شما مالک این آگهی هستید و نمی‌توانید با خودتان چت کنید!");
            return;
        }

        // دریافت پیام اولیه
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle("شروع گفتگو با فروشنده");
        dialog.setHeaderText("گفتگو برای آگهی: " + advertisement.getTitle()); //
        dialog.setContentText("متن پیام خود را وارد کنید:");

        dialog.showAndWait().ifPresent(messageText -> {
            if (messageText.trim().isEmpty()) {
                showAlert("خطا", "متن پیام نمی‌تواند خالی باشد!");
                return;
            }

            try {
                // ارسال زنده پیام اولیه و ساخت چت‌روم واقعی در بک‌اند
                ChatSession session = networkService.startOrGetChat(advertisement.getId(), messageText);

                showAlert("موفقیت", "پیام شما با موفقیت ارسال شد و گفتگو آغاز گردید.");

                // هدایت خودکار کاربر به صفحه صندوق پیام‌ها
                mainController.showChats(); //

            } catch (Exception e) {
                showAlert("خطا در ایجاد گفتگو", e.getMessage());
            }
        });
    }

    private void showAlert(String title, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleToggleFavorite() {
        isFavorite = !isFavorite;
        if (isFavorite) {
            btnFavorite.setText("⭐ حذف از علاقه‌مندی‌ها");
            btnFavorite.setStyle("-fx-background-color: #FF4757; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 15;");
            statusLabel.setStyle("-fx-text-fill: #2ED573;");
            statusLabel.setText("به علاقه‌مندی‌ها اضافه شد.");
        } else {
            btnFavorite.setText("⭐ افزودن به علاقه‌مندی‌ها");
            btnFavorite.setStyle("-fx-background-color: #FFB142; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 15;");
            statusLabel.setStyle("-fx-text-fill: #FF7F50;");
            statusLabel.setText("از علاقه‌مندی‌ها حذف شد.");
        }
    }

    /**
     * ارسال واقعی امتیاز و بازخورد به سرور
     */
    @FXML
    private void handleSubmitReview() {
        String ratingStr = ratingField.getText().trim(); //
        String comment = commentField.getText().trim(); //

        if (ratingStr.isEmpty() || comment.isEmpty()) { //
            statusLabel.setStyle("-fx-text-fill: #FF4757;");
            statusLabel.setText("لطفاً امتیاز و متن نظر را وارد کنید!");
            return;
        }

        try {
            int score = Integer.parseInt(ratingStr); //
            if (score < 1 || score > 5) { //
                statusLabel.setStyle("-fx-text-fill: #FF4757;");
                statusLabel.setText("امتیاز باید بین ۱ تا ۵ باشد!");
                return;
            }

            // ارسال امتیاز و متن به بک‌اَند واقعی با استفاده از NetworkService
            HttpResponse<String> response = networkService.submitReview(advertisement.getId(), score, comment);

            if (response.statusCode() == 200 || response.statusCode() == 201) { //
                statusLabel.setStyle("-fx-text-fill: #2ED573;");
                statusLabel.setText("امتیاز و نظر شما با موفقیت در سرور ثبت شد!");

                ratingField.clear(); //
                commentField.clear(); //
            } else {
                statusLabel.setStyle("-fx-text-fill: #FF4757;");
                statusLabel.setText("خطا از سمت سرور: " + response.body()); //
            }

        } catch (NumberFormatException e) {
            statusLabel.setStyle("-fx-text-fill: #FF4757;");
            statusLabel.setText("امتیاز وارد شده باید عدد باشد!"); //
        } catch(Exception e) {
            statusLabel.setStyle("-fx-text-fill: #FF4757;");
            statusLabel.setText("خطا در ارتباط با سرور!"); //
        }
    }

    // ================= دکمه‌های مخصوص مالک آگهی =================

    @FXML
    private void handleEditAd() {
        statusLabel.setStyle("-fx-text-fill: #54A0FF;");
        statusLabel.setText("وارد صفحه ویرایش آگهی شدید.");
        // در صورت نیاز به افزودن فرانت ویرایش، می‌توانید لودر صفحه ویرایش را اینجا بنویسید.
    }

    /**
     * حذف واقعی آگهی روی سرور
     */
    @FXML
    private void handleDeleteAd() {
        try {
            HttpResponse<String> response = networkService.deleteAdvertisement(advertisement.getId());

            if (response.statusCode() == 200) {
                statusLabel.setStyle("-fx-text-fill: #FF4757;");
                statusLabel.setText("آگهی با موفقیت از سیستم حذف شد!");

                // بازگشت به لیست آگهی‌ها پس از ۱.۵ ثانیه
                new Thread(() -> {
                    try {
                        Thread.sleep(1500);
                        javafx.application.Platform.runLater(this::handleBack);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                statusLabel.setStyle("-fx-text-fill: #FF4757;");
                statusLabel.setText("خطا در حذف آگهی: " + response.body());
            }
        } catch (Exception e) {
            statusLabel.setStyle("-fx-text-fill: #FF4757;");
            statusLabel.setText("خطا در ارتباط با سرور!");
        }
    }

    /**
     * ثبت زنده وضعیت فروخته شده کالا روی سرور
     */
    @FXML
    private void handleMarkAsSold() {
        try {
            HttpResponse<String> response = networkService.markAdAsSold(advertisement.getId());

            if (response.statusCode() == 200) {
                advertisement.setSold(true); //
                updateSoldUI(); //
                statusLabel.setStyle("-fx-text-fill: #1DD1A1;");
                statusLabel.setText("وضعیت کالا به «فروخته شده» تغییر یافت!"); //
            } else {
                statusLabel.setStyle("-fx-text-fill: #FF4757;");
                statusLabel.setText("خطا در ثبت وضعیت فروخته شده: " + response.body());
            }
        } catch (Exception e) {
            statusLabel.setStyle("-fx-text-fill: #FF4757;");
            statusLabel.setText("خطا در ارتباط با سرور!");
        }
    }
}