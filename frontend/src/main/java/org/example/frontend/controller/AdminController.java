package org.example.frontend.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.frontend.model.Advertisement;
import org.example.frontend.network.NetworkService;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

public class AdminController {

    @FXML private ListView<Advertisement> pendingListView;
    @FXML private Label titleLabel;
    @FXML private Label categoryLabel;
    @FXML private Label cityLabel;
    @FXML private Label priceLabel;
    @FXML private TextArea descriptionArea;
    @FXML private Label imageLabel;

    @FXML private Button btnApprove;
    @FXML private Button btnReject;

    private Advertisement selectedAd = null;
    private final NetworkService networkService = new NetworkService();

    @FXML
    public void initialize() {
        setupListView();
        loadPendingAds();
    }

    private void setupListView() {
        // زیباسازی نمایش عنوان در ListView
        pendingListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Advertisement item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("📦 " + item.getTitle());
                }
            }
        });

        // شنونده برای کلیک روی هر آگهی در لیست و نمایش جزئیات آن
        pendingListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showAdDetails(newVal);
            }
        });
    }

    /**
     * دریافت لیست زنده آگهی‌های PENDING از سرور
     */
    private void loadPendingAds() {
        pendingListView.getItems().clear();

        try {
            List<Advertisement> pendingAds = networkService.getPendingAdvertisements();
            pendingListView.getItems().addAll(pendingAds);

            if (pendingListView.getItems().isEmpty()) {
                clearDetails();
                titleLabel.setText("هیچ آگهی در انتظار بررسی وجود ندارد.");
                btnApprove.setDisable(true);
                btnReject.setDisable(true);
            } else {
                btnApprove.setDisable(false);
                btnReject.setDisable(false);
                pendingListView.getSelectionModel().selectFirst();
            }
        } catch (Exception e) {
            e.printStackTrace();
            titleLabel.setText("خطا در بارگذاری اطلاعات از سرور!");
        }
    }

    private void showAdDetails(Advertisement ad) {
        this.selectedAd = ad;
        titleLabel.setText(ad.getTitle());
        categoryLabel.setText(String.valueOf(ad.getCategoryId()));
        cityLabel.setText(String.valueOf(ad.getCityId()));
        priceLabel.setText(String.format("%,.0f تومان", ad.getPrice()));
        descriptionArea.setText(ad.getDescription());
        imageLabel.setText("🖼️ بدون تصویر یا پیش‌فرض");
    }

    private void clearDetails() {
        selectedAd = null;
        titleLabel.setText("-");
        categoryLabel.setText("-");
        cityLabel.setText("-");
        priceLabel.setText("-");
        descriptionArea.clear();
        imageLabel.setText("تصویری انتخاب نشده است");
    }

    /**
     * تایید نهایی آگهی روی دیتابیس سرور
     */
    @FXML
    private void handleApprove() {
        if (selectedAd == null) return;

        try {
            HttpResponse<String> response = networkService.approveAdvertisement(selectedAd.getId());

            if (response.statusCode() == 200) {
                System.out.println("آگهی '" + selectedAd.getTitle() + "' با موفقیت تایید شد.");
                loadPendingAds(); // رفرش لیست
            } else {
                System.err.println("خطا در تایید آگهی: " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * رد آگهی و ارسال دلیل رد به سرور
     */
    @FXML
    private void handleReject() {
        if (selectedAd == null) return;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("رد کردن آگهی");
        dialog.setHeaderText("علت رد کردن آگهی '" + selectedAd.getTitle() + "' چیست؟");
        dialog.setContentText("علت:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String reason = result.get().trim();

            try {
                HttpResponse<String> response = networkService.rejectAdvertisement(selectedAd.getId(), reason);

                if (response.statusCode() == 200) {
                    System.out.println("آگهی رد شد. علت: " + reason);
                    loadPendingAds(); // رفرش لیست
                } else {
                    System.err.println("خطا در رد آگهی: " + response.body());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (result.isPresent()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("برای رد آگهی، باید حتماً دلیلی بنویسید!");
            alert.showAndWait();
        }
    }
}