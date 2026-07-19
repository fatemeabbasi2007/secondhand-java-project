package org.example.frontend.controller;

import org.example.frontend.model.AdResponse;
import org.example.frontend.security.SessionManager;
import org.example.frontend.service.AuthService;
import org.example.frontend.service.PublicAdService;
import org.example.frontend.util.NavigationService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import java.util.List;

public class MainController {

    @FXML private TextField searchField;
    @FXML private TextField categoryFilterField;
    @FXML private TextField cityFilterField;
    @FXML private TextField minPriceField;
    @FXML private TextField maxPriceField;

    @FXML private ListView<AdResponse> adsListView;

    // بخش نمایش جزئیات آگهی انتخاب شده در صفحه اصلی
    @FXML private Label titleLabel;
    @FXML private Label priceLabel;
    @FXML private Label cityLabel;
    @FXML private Label categoryLabel;
    @FXML private Label ownerLabel;
    @FXML private TextArea descriptionArea;

    private final PublicAdService adService = new PublicAdService();
    private final ObservableList<AdResponse> observableList = FXCollections.observableArrayList();
    private AdResponse selectedAd;

    @FXML
    public void initialize() {
        adsListView.setItems(observableList);

        // سفارشی‌سازی سلول‌های لیست
        adsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(AdResponse item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitle() + " - " + String.format("%,.0f تومان", item.getPrice()));
                }
            }
        });

        // ۱. تک‌کلیک: نمایش پیش‌نمایش در سمت راست صفحه اصلی
        adsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                showAdDetails(newValue); // متد قدیمی شما
            }
        });

        // ۲. دابل‌کلیک: انتقال به صفحه اختصاصی جزئیات آگهی
        adsListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // بررسی دابل‌کلیک
                AdResponse selected = adsListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    // ساخت یک ActionEvent مصنوعی برای متد ناوبری
                    ActionEvent actionEvent = new ActionEvent(event.getSource(), null);
                    openAdDetailsPage(actionEvent, selected.getId()); // متد جدید
                }
            }
        });

        // بارگذاری اولیه کل آگهی‌های فعال
        onSearchClick(null);
    }

    // مدیریت کلیک دکمه جست‌وجو و اعمال فیلترها
    @FXML
    public void onSearchClick(ActionEvent event) {
        String query = searchField.getText().trim();
        String category = categoryFilterField.getText().trim();
        String city = cityFilterField.getText().trim();
        String minPriceStr = minPriceField.getText().trim();
        String maxPriceStr = maxPriceField.getText().trim();

        Double minPrice = null;
        Double maxPrice = null;

        try {
            if (!minPriceStr.isEmpty()) minPrice = Double.parseDouble(minPriceStr);
            if (!maxPriceStr.isEmpty()) maxPrice = Double.parseDouble(maxPriceStr);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "خطای ورودی", "قیمت‌های فیلتر باید عددی باشند.");
            return;
        }

        try {
            List<AdResponse> ads = adService.getActiveAdvertisements(query, category, city, minPrice, maxPrice);
            observableList.setAll(ads);
            clearDetails();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "خطا در بارگذاری آگهی‌ها", e.getMessage());
        }
    }

    private void showAdDetails(AdResponse ad) {
        this.selectedAd = ad;
        titleLabel.setText(ad.getTitle());
        priceLabel.setText(String.format("%,.0f تومان", ad.getPrice()));
        cityLabel.setText(ad.getCity());
        categoryLabel.setText(ad.getCategory());
        ownerLabel.setText(ad.getOwnerUsername());
        descriptionArea.setText(ad.getDescription());
    }

    // قرار دادن این منطق در زمان کلیک بر روی لیست آگهی‌ها در MainController
    private void openAdDetailsPage(ActionEvent event, Long adId) {
        try {
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/org/example/frontend/ad-details.fxml"));
            javafx.scene.Parent root = loader.load();

            // دریافت کنترلر صفحه جزئیات و پاس دادن آی‌دی آگهی به آن
            AdDetailsController controller = loader.getController();
            controller.setAdId(adId);

            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("جزئیات آگهی");
            stage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    // دکمه انتقال به صفحه ثبت آگهی جدید
    @FXML
    public void onNewAdClick(ActionEvent event) {
        NavigationService.navigate(event, "new-advertisement.fxml", "ثبت آگهی جدید");
    }

//    @FXML
//    public void onLogoutClick(ActionEvent event) {
//        System.out.println("خروج از حساب...");
//        // پاک کردن اطلاعات نشست کاربر
//        SessionManager.getInstance().cleanSession();
//        // هدایت به صفحه لاگین
//        NavigationService.navigate(event, "login.fxml", "ورود به سامانه");
//    }

    @FXML
    public void onGoToChatsClick(ActionEvent event) {
        NavigationService.navigate(event, "chat-view.fxml", "صندوق پیام‌ها");
    }

    @FXML
    public void onGoToFavoritesClick(ActionEvent event) {
        NavigationService.navigate(event, "favorites-view.fxml", "علاقه‌مندی‌های من");
    }


    private final AuthService authService = new AuthService();
    @FXML
    public void onLogoutClick(ActionEvent event) {
        try {
            // ۱. اول به سرور می‌گوییم که سشن را باطل کند
            authService.logout();

            // ۲. حالا اطلاعات محلی برنامه را پاک می‌کنیم
            SessionManager.getInstance().cleanSession();

            System.out.println("خروج موفقیت‌آمیز بود.");

            // ۳. هدایت کاربر به صفحه لاگین
            NavigationService.navigate(event, "login.fxml", "صفحه ورود به سامانه");

        } catch (Exception e) {
            // حتی اگر سرور خطا داد، برای اطمینان سشن فرانت‌اندمان را پاک می‌کنیم و خارج می‌شویم
            SessionManager.getInstance().cleanSession();
            NavigationService.navigate(event, "login.fxml", "صفحه ورود به سامانه");
        }
    }

    private void clearDetails() {
        this.selectedAd = null;
        titleLabel.setText("-");
        priceLabel.setText("-");
        cityLabel.setText("-");
        categoryLabel.setText("-");
        ownerLabel.setText("-");
        descriptionArea.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}