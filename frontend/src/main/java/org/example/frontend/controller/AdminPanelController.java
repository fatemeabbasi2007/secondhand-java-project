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

    // فیلدهای مربوط به مدیریت کاربران (جدید)
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
        // تنظیم لیست آگهی‌ها
        pendingAdsListView.setItems(pendingAdsList);
        pendingAdsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(PendingAdResponse item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitle() + " - (کاربر: " + item.getOwnerUsername() + ")");
                }
            }
        });
        pendingAdsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) showAdDetails(newVal);
        });

        // تنظیم لیست کاربران
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
        titleLabel.setText(ad.getTitle());
        priceLabel.setText(String.format("%,.0f تومان", ad.getPrice()));
        cityLabel.setText(ad.getCity());
        categoryLabel.setText(ad.getCategory());
        ownerLabel.setText(ad.getOwnerUsername());
        descriptionArea.setText(ad.getDescription());
        reasonField.clear();
    }

    private void showUserDetails(UserResponse user) {
        this.selectedUser = user;
        userFullNameLabel.setText(user.getFullName());
        userUsernameLabel.setText(user.getUsername());
        userPhoneLabel.setText(user.getPhoneNumber());
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

    @FXML
    public void onApproveClick(ActionEvent event) {
        if (selectedAd == null) return;
        try {
            adminService.reviewAdvertisement(selectedAd.getId(), true, null);
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
            adminService.reviewAdvertisement(selectedAd.getId(), false, reason);
            showAlert(Alert.AlertType.INFORMATION, "موفق", "آگهی رد شد.");
            loadPendingAds();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "خطا", e.getMessage());
        }
    }

    // قابلیت حذف مستقیم آگهی نامناسب
    @FXML
    public void onDeleteAdClick(ActionEvent event) {
        if (selectedAd == null) {
            showAlert(Alert.AlertType.WARNING, "انتخاب آگهی", "لطفاً آگهی را انتخاب کنید.");
            return;
        }

        // نمایش تاییدیه برای جلوگیری از حذف تصادفی
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

    // قابلیت مسدود / رفع مسدود کردن کاربر (جدید)
    @FXML
    public void onToggleBlockClick(ActionEvent event) {
        if (selectedUser == null) return;

        boolean shouldBlock = !selectedUser.isBlocked();
        String actionName = shouldBlock ? "مسدود" : "فعال";

        try {
            adminService.toggleUserBlockStatus(selectedUser.getId(), shouldBlock);
            showAlert(Alert.AlertType.INFORMATION, "موفق", "کاربر با موفقیت " + actionName + " شد.");
            loadAllUsers(); // به‌روزرسانی لیست کاربران
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "خطا در تغییر وضعیت", e.getMessage());
        }
    }

    @FXML
    public void onLogoutClick(ActionEvent event) {
        System.out.println("خروج مدیر...");
        // پاک کردن اطلاعات نشست ادمین در فرانت‌اند
        SessionManager.getInstance().cleanSession();
        // هدایت به صفحه لاگین
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