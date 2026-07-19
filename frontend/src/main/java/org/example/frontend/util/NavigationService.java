package org.example.frontend.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import java.io.IOException;

public class NavigationService {

    /**
     * متد عمومی برای تغییر صفحه در جاوااف‌ایکس
     * @param event رویداد کلیک دکمه (ActionEvent)
     * @param fxmlFileName نام فایل fxml (مثلاً "login.fxml")
     * @param title عنوان پنجره جدید
     */
    public static void navigate(ActionEvent event, String fxmlFileName, String title) {
        try {
            // پیدا کردن استیج (Stage) فعلی از روی دکمه‌ای که کلیک شده است
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // بارگذاری فایل FXML از پوشه منابع (resources)
            FXMLLoader loader = new FXMLLoader(NavigationService.class.getResource("/org/example/frontend/" + fxmlFileName));
            Parent root = loader.load();

            // قرار دادن صفحه جدید روی پنجره فعلی
            Scene scene = new Scene(root);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("خطا در بارگذاری صفحه: " + fxmlFileName + " | علت: " + e.getMessage());
        }
    }
}