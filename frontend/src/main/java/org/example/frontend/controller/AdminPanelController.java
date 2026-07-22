package org.example.frontend.controller;

import org.example.frontend.model.PendingAdResponse;
import org.example.frontend.model.UserResponse;
import org.example.frontend.security.SessionManager;
import org.example.frontend.service.AdminService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import org.example.frontend.util.NavigationService;

import java.util.List;

public class AdminPanelController {

    // فیلدهای مربوط به آگهی‌های در انتظار بررسی
    @FXML private ListView<PendingAdResponse> pendingAdsListView;
    @FXML private Label titleLabel;
    @FXML private Label priceLabel;
    @FXML private Label cityLabel;
    @FXML private Label categoryLabel;
    @FXML private Label ownerLabel;
    @FXML private TextArea descriptionArea;
    @FXML private TextField reasonField;

    // فیلدهای مربوط به مدیریت کاربران
    @FXML private ListView<UserResponse> usersListView;
    @FXML private Label userFullNameLabel;
    @FXML private Label userUsernameLabel;
    @FXML private Label userPhoneLabel;
    @FXML private Label userEmailLabel;
    @FXML private Label userStatusLabel;
    @FXML private Button blockToggleButton;

    private final AdminService adminService = new AdminService();
    private final ObservableList<PendingAdResponse> pendingAdsList = FXCollections.observableArrayList();
    private final ObservableList<UserResponse> usersList = FXCollections.observableArrayList();

    private PendingAdResponse selectedAd;
    private UserResponse selectedUser;

    @FXML
    public void initialize() {
        // ۱. تنظیم لیست آگهی‌ها (نمایش عنوان و نام کاربری)
        pendingAdsListView.setItems(pendingAdsList);
        pendingAdsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(PendingAdResponse item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // 👈 استفاده از متد جدید جهت دریافت نام کاربری
                    setText(item.getTitle() + " - (کاربر: " + getOwnerDisplayName(item) + ")");
                }
            }
        });
        pendingAdsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) showAdDetails(newVal);
        });

        // ۲. تنظیم لیست کاربران
        usersListView.setItems(usersList);
        usersListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(UserResponse item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String status = item.isBlocked() ? "[مسدود شده]" : "[فعال]";
                    setText(item.getUsername() + " - " + status);
                }
            }
        });
        usersListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) showUserDetails(newVal);
        });

        // بارگذاری داده‌های اولیه
        loadPendingAds();
        loadAllUsers();
    }

    private void loadPendingAds() {
        try {
            List<PendingAdResponse> ads = adminService.getPendingAdvertisements();
            pendingAdsList.setAll(ads);
            clearAdDetails();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "خطا در بارگذاری", e.getMessage());
        }
    }

    private void loadAllUsers() {
        try {
            List<UserResponse> users = adminService.getAllUsers();
            usersList.setAll(users);
            clearUserDetails();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "خطا در بارگذاری کاربران", e.getMessage());
        }
    }

    private void showAdDetails(PendingAdResponse ad) {
        this.selectedAd = ad;
        titleLabel.setText(ad.getTitle() != null ? ad.getTitle() : "-");
        priceLabel.setText(String.format("%,.0f تومان", ad.getPrice()));
        cityLabel.setText(ad.getCity() != null ? ad.getCity() : "-");
        categoryLabel.setText(ad.getCategory() != null ? ad.getCategory() : "-");

        // 👈 نمایش نام کاربری در پنل جزئیات آگهی
        ownerLabel.setText(getOwnerDisplayName(ad));

        descriptionArea.setText(ad.getDescription() != null ? ad.getDescription() : "");
        reasonField.clear();
    }

    private void showUserDetails(UserResponse user) {
        this.selectedUser = user;
        userFullNameLabel.setText(user.getFullName() != null ? user.getFullName() : "-");
        userUsernameLabel.setText(user.getUsername() != null ? user.getUsername() : "-");
        userPhoneLabel.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "-");
        userEmailLabel.setText(user.getEmail() != null ? user.getEmail() : "ثبت نشده");

        if (user.isBlocked()) {
            userStatusLabel.setText("مسدود شده");
            userStatusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            blockToggleButton.setText("رفع مسدودیت");
            blockToggleButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        } else {
            userStatusLabel.setText("فعال");
            userStatusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            blockToggleButton.setText("مسدود کردن کاربر");
            blockToggleButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        }
    }

    /**
     * متد کمکی جهت استخراج ایمن نام کاربری ثبت‌کننده آگهی
     */
    private String getOwnerDisplayName(PendingAdResponse ad) {
        if (ad == null) return "-";
        if (ad.getOwnerUsername() != null && !ad.getOwnerUsername().trim().isEmpty()) {
            return ad.getOwnerUsername();
        }
//        return ad.getOwnerId() != null ? ad.getOwnerId() : "نامشخص";
        return  "نامشخص";

    }

    @FXML
    public void onApproveClick(ActionEvent event) {
        if (selectedAd == null) return;
        try {
            adminService.approveAdvertisement(selectedAd.getId().toString());
            showAlert(Alert.AlertType.INFORMATION, "موفق", "آگهی با موفقیت تایید شد.");
            loadPendingAds();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "خطای تایید", e.getMessage());
        }
    }

    @FXML
    public void onRejectClick(ActionEvent event) {
        if (selectedAd == null) return;
        String reason = reasonField.getText().trim();
        if (reason.isEmpty()) {
            reasonField.setStyle("-fx-border-color: red; -fx-border-width: 1.5px;");
            showAlert(Alert.AlertType.WARNING, "علت رد", "لطفاً علت رد را بنویسید.");
            return;
        }
        reasonField.setStyle("");

        try {
            adminService.rejectAdvertisement(selectedAd.getId().toString(), reason);
            showAlert(Alert.AlertType.INFORMATION, "موفق", "آگهی رد شد.");
            loadPendingAds();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "خطا", e.getMessage());
        }
    }

    @FXML
    public void onDeleteAdClick(ActionEvent event) {
        if (selectedAd == null) {
            showAlert(Alert.AlertType.WARNING, "انتخاب آگهی", "لطفاً آگهی را انتخاب کنید.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "آیا از حذف این آگهی نامناسب مطمئن هستید؟", ButtonType.YES, ButtonType.NO);
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    adminService.deleteInappropriateAdvertisement(selectedAd.getId());
                    showAlert(Alert.AlertType.INFORMATION, "موفق", "آگهی نامناسب با موفقیت حذف شد.");
                    loadPendingAds();
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "خطا در حذف", e.getMessage());
                }
            }
        });
    }

    @FXML
    public void onToggleBlockClick(ActionEvent event) {
        if (selectedUser == null) return;

        boolean shouldBlock = !selectedUser.isBlocked();
        String actionName = shouldBlock ? "مسدود" : "فعال";

        try {
            adminService.toggleUserBlockStatus(selectedUser.getId(), shouldBlock);
            showAlert(Alert.AlertType.INFORMATION, "موفق", "کاربر با موفقیت " + actionName + " شد.");
            loadAllUsers();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "خطا در تغییر وضعیت", e.getMessage());
        }
    }

    @FXML
    public void onLogoutClick(ActionEvent event) {
        System.out.println("خروج مدیر...");
        SessionManager.getInstance().cleanSession();
        NavigationService.navigate(event, "login.fxml", "ورود به سامانه");
    }

    private void clearAdDetails() {
        this.selectedAd = null;
        titleLabel.setText("-");
        priceLabel.setText("-");
        cityLabel.setText("-");
        categoryLabel.setText("-");
        ownerLabel.setText("-");
        descriptionArea.clear();
        reasonField.clear();
    }

    private void clearUserDetails() {
        this.selectedUser = null;
        userFullNameLabel.setText("-");
        userUsernameLabel.setText("-");
        userPhoneLabel.setText("-");
        userEmailLabel.setText("-");
        userStatusLabel.setText("-");
        userStatusLabel.setStyle("");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}