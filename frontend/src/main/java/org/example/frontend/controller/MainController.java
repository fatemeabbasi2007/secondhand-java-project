package org.example.frontend.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import org.example.frontend.model.Advertisement;
import org.example.frontend.network.NetworkService;
import org.example.frontend.network.SessionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    @FXML private Label roleBadge;
    @FXML private Label pageTitle;
    @FXML private FlowPane productsContainer;

    // دکمه‌های منوی سمت چپ (دقیقاً با همین نام‌ها)
    @FXML private Button btnDashboard;
    @FXML private Button btnAddItem;
    @FXML private Button btnFavorites;
    @FXML private Button btnChats;
    @FXML private Button btnPendingAds;
    @FXML private Button btnManageUsers;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterCategoryComboBox;
    @FXML private ComboBox<String> filterCityComboBox;
    @FXML private TextField minPriceField;
    @FXML private TextField maxPriceField;

    // ۱. تعریف شیء اتصال به شبکه واقعی
    private final NetworkService networkService = new NetworkService();

    @FXML
    public void initialize() {
        applyPermissions(); //
        loadProducts(); // فراخوانی متد لود آگهی‌ها
    }

    @FXML
    private void showDashboard() {
        loadProducts(); // وقتی روی دکمه داشبورد کلیک شد، کالاها را لود کن
    }

    /**
     * لود کردن و نمایش کارت‌های آگهی در صفحه اصلی (دریافت زنده از بک‌اَند)
     */
    public void loadProducts() {
        if (productsContainer == null) return; //

        productsContainer.getChildren().clear(); //
        if (pageTitle != null) {
            pageTitle.setText("کالاهای اخیر"); //
        }

        try {
            // ۲. به جای داده‌های تست، لیست را مستقیماً از سرور می‌گیریم
            List<Advertisement> allAds = networkService.getAllAdvertisements();

            for (Advertisement ad : allAds) {
                // نمایش آگهی فقط در صورتی که APPROVED باشد و فروخته نشده باشد (isSold == false)
                if ("APPROVED".equalsIgnoreCase(ad.getStatus()) && !ad.isSold()) { //
                    productsContainer.getChildren().add(createProductCard(ad)); //
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (pageTitle != null) {
                pageTitle.setText("خطا در بارگذاری کالاهای اخیر از سرور!");
            }
        }
    }

    /**
     * ساخت کارت کالا به صورت کاملاً پویا و گرافیکی بر اساس مدل Advertisement
     */
    private VBox createProductCard(Advertisement ad) {
        VBox card = new VBox(8); //
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);"); //
        card.setPrefWidth(200); //
        card.setPrefHeight(180); //
        card.setPadding(new javafx.geometry.Insets(15)); //

        Label titleLabel = new Label(ad.getTitle()); //
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: #2F3542;"); //

        Label priceLabel = new Label(String.format("%,.0f تومان", ad.getPrice())); //
        priceLabel.setStyle("-fx-text-fill: #3742FA; -fx-font-weight: bold; -fx-font-size: 13;"); //

        Label descLabel = new Label(ad.getDescription()); //
        descLabel.setWrapText(true); //
        descLabel.setStyle("-fx-text-fill: #747D8C; -fx-font-size: 11;"); //

        Button viewButton = new Button("مشاهده جزئیات"); //
        viewButton.setStyle("-fx-background-color: #F1F2F6; -fx-text-fill: #2F3542; -fx-background-radius: 5;"); //
        viewButton.setMaxWidth(Double.MAX_VALUE); //

        // دکمه به صورت کاملاً پویا کل شیء آگهی را به متد جزئیات می‌فرستد
        viewButton.setOnAction(event -> showProductDetails(ad)); //

        card.getChildren().addAll(titleLabel, priceLabel, descLabel, viewButton); //
        return card;
    }

    /**
     * باز کردن جزئیات یک آگهی به صورت پویا
     */
    public void showProductDetails(Advertisement ad) {
        try {
            pageTitle.setText("جزئیات آگهی"); //

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/frontend/product-details-view.fxml")); //
            VBox detailsView = loader.load(); //

            ProductDetailsController controller = loader.getController(); //
            // پاس دادن مستقیم شیء آگهی به کنترلر جزئیات
            controller.setProductData(ad, this); //

            productsContainer.getChildren().clear(); //
            productsContainer.getChildren().add(detailsView); //

        } catch (IOException e) {
            e.printStackTrace();
            pageTitle.setText("خطا در بارگذاری جزئیات کالا!"); //
        }
    }

    /**
     * لود کردن فرم ثبت آگهی جدید به صورت داینامیک در وسط صفحه
     */
    @FXML
    private void showAddItemForm() {
        try {
            pageTitle.setText("ثبت کالای جدید"); //

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/frontend/add-item-view.fxml")); //
            VBox addItemForm = loader.load(); //

            productsContainer.getChildren().clear(); //
            productsContainer.getChildren().add(addItemForm); //

        } catch (IOException e) {
            e.printStackTrace();
            pageTitle.setText("خطا در بارگذاری فرم ثبت کالا!"); //
        }
    }

    /**
     * لود کردن بخش چت‌ها به صورت داینامیک در وسط صفحه
     */
    @FXML
    void showChats() {
        try {
            pageTitle.setText("گفتگوهای من"); //

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/frontend/chats-view.fxml")); //
            HBox chatsView = loader.load(); //

            productsContainer.getChildren().clear(); //
            productsContainer.getChildren().add(chatsView); //

        } catch (IOException e) {
            e.printStackTrace();
            pageTitle.setText("خطا در بارگذاری بخش چت!"); //
        }
    }

    /**
     * نمایش لیست علاقه‌مندی‌ها
     */
    @FXML
    private void showFavorites() {
        pageTitle.setText("آگهی‌های نشان‌شده (علاقه‌مندی‌ها)"); //
        productsContainer.getChildren().clear(); //

        Label label = new Label("اینجا لیست آگهی‌هایی که نشان کرده‌اید نمایش داده می‌شود."); //
        label.setStyle("-fx-text-fill: #747D8C; -fx-font-size: 14px; -fx-padding: 20;"); //
        productsContainer.getChildren().add(label); //
    }

    /**
     * نمایش آگهی‌های منتظر تایید (مخصوص ادمین)
     */
    @FXML
    private void showPendingAds() {
        pageTitle.setText("تایید آگهی‌های جدید (پنل مدیریت)"); //
        productsContainer.getChildren().clear(); //

        Label label = new Label("لیست آگهی‌های ثبت شده که نیاز به تایید ادمین دارند."); //
        label.setStyle("-fx-text-fill: #FF7F50; -fx-font-size: 14px; -fx-padding: 20;"); //
        productsContainer.getChildren().add(label); //
    }

    /**
     * نمایش مدیریت کاربران (مخصوص ادمین)
     */
    @FXML
    private void showManageUsers() {
        pageTitle.setText("مدیریت کاربران سیستم (پنل مدیریت)"); //
        productsContainer.getChildren().clear(); //

        Label label = new Label("لیست کاربران عضو سامانه جهت مسدودسازی یا ارتقاء به ادمین."); //
        label.setStyle("-fx-text-fill: #FF7F50; -fx-font-size: 14px; -fx-padding: 20;"); //
        productsContainer.getChildren().add(label); //
    }

    /**
     * جستجو و فیلتر کردن آگهی‌ها به صورت آنلاین از طریق سرور
     */
    @FXML
    private void handleSearchAndFilter() {
        if (productsContainer == null) return; //

        productsContainer.getChildren().clear(); //
        String query = (searchField != null) ? searchField.getText().trim() : ""; //
        String selectedCategory = (filterCategoryComboBox != null) ? filterCategoryComboBox.getValue() : null; //
        String selectedCity = (filterCityComboBox != null) ? filterCityComboBox.getValue() : null; //
        String minPriceStr = (minPriceField!= null) ? minPriceField.getText().trim() : ""; //
        String maxPriceStr = (maxPriceField != null) ? maxPriceField.getText().trim() : ""; //

        Double minPrice = null;
        Double maxPrice = null;

        try {
            if (!minPriceStr.isEmpty()) minPrice = Double.parseDouble(minPriceStr); //
            if (!maxPriceStr.isEmpty()) maxPrice = Double.parseDouble(maxPriceStr); //
        } catch (NumberFormatException e) {
            System.err.println("محدوده قیمت نامعتبر است!"); //
        }

        try {
            // ۳. دریافت آگهی‌های فیلتر شده مستقیماً از بک‌اَند
            List<Advertisement> filteredAds = networkService.searchAdvertisements(query, selectedCategory, selectedCity, minPrice, maxPrice);

            for (Advertisement ad : filteredAds) {
                // شرط ثانویه: فقط آگهی‌های تایید شده و فروخته نشده نمایش داده شوند
                if ("APPROVED".equalsIgnoreCase(ad.getStatus()) && !ad.isSold()) { //
                    productsContainer.getChildren().add(createProductCard(ad)); //
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("خطا در انجام فیلتر زنده از بک‌اند!");
        }
    }

    /**
     * دکمه ریست فیلترها
     */
    @FXML
    private void handleResetFilters() {
        if (searchField != null) searchField.clear(); //
        if (filterCategoryComboBox != null) filterCategoryComboBox.getSelectionModel().clearSelection(); //
        if (filterCityComboBox != null) filterCityComboBox.getSelectionModel().clearSelection(); //
        if (minPriceField != null) minPriceField.clear(); //
        if (maxPriceField != null) maxPriceField.clear(); //

        loadProducts(); // برگشت به حالت لود ساده (همه فعال‌ها)
    }

    /**
     * اعمال سطح دسترسی‌ها بر اساس نقش کاربر لاگین شده
     */
    private void applyPermissions() {
        boolean isAdmin = SessionManager.isAdmin(); //

        if (isAdmin) {
            roleBadge.setText("نقش: مدیر سیستم"); //
            roleBadge.setStyle("-fx-text-fill: #FF4757; -fx-font-weight: bold;"); //
        } else {
            roleBadge.setText("نقش: کاربر عادی"); //
            roleBadge.setStyle("-fx-text-fill: #2ED573; -fx-font-weight: bold;"); //
        }

        // دکمه آگهی‌های منتظر تایید
        if (btnPendingAds != null) {
            btnPendingAds.setVisible(isAdmin); //
            btnPendingAds.setManaged(isAdmin); //
        }

        // دکمه مدیریت کاربران
        if (btnManageUsers != null) {
            btnManageUsers.setVisible(isAdmin); //
            btnManageUsers.setManaged(isAdmin); //
        }
    }

    @FXML
    private void handleLogout() {
        try {
            SessionManager.clearSession(); //

            // بازگشت به صفحه لاگین
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/frontend/login-view.fxml")); //
            javafx.scene.Parent loginRoot = loader.load(); //

            javafx.stage.Stage stage = (javafx.stage.Stage) roleBadge.getScene().getWindow(); //
            stage.setScene(new javafx.scene.Scene(loginRoot, 400, 450)); //
            stage.setTitle("ورود به سامانه"); //
            stage.centerOnScreen(); //
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}