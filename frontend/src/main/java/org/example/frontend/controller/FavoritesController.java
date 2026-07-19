package org.example.frontend.controller;

import org.example.frontend.model.FavoriteResponse;
import org.example.frontend.service.FavoriteService;
import org.example.frontend.util.NavigationService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import java.util.List;

public class FavoritesController {

    @FXML private ListView<FavoriteResponse> favoritesListView;

    private final FavoriteService favoriteService = new FavoriteService();
    private final ObservableList<FavoriteResponse> observableList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        favoritesListView.setItems(observableList);

        // شخصی‌سازی نمایش آیتم‌های لیست به همراه دکمه حذف یا جزییات آگهی
        favoritesListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(FavoriteResponse item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%s - %s (قیمت: %,.0f تومان)",
                            item.getTitle(), item.getCity(), item.getPrice()));
                }
            }
        });

        loadFavorites();
    }

    private void loadFavorites() {
        try {
            List<FavoriteResponse> list = favoriteService.getFavorites();
            observableList.setAll(list);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "خطا در دریافت علاقه‌مندی‌ها", e.getMessage());
        }
    }

    // حذف آگهی نشانه‌گذاری شده
    @FXML
    public void onRemoveFavoriteClick(ActionEvent event) {
        FavoriteResponse selected = favoritesListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "انتخاب آیتم", "لطفاً ابتدا یک آگهی را از لیست انتخاب کنید.");
            return;
        }

        try {
            favoriteService.removeFromFavorites(selected.getFavoriteId());
            showAlert(Alert.AlertType.INFORMATION, "موفق", "آگهی با موفقیت از لیست علاقه‌مندی‌ها حذف شد.");
            loadFavorites(); // بروزرسانی مجدد لیست
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "خطا در حذف", e.getMessage());
        }
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