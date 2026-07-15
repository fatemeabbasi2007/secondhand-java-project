package org.example.frontend.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainController {

    @FXML
    private void handleLogout() {
        try {
            // بازگشت به صفحه ورود
            Stage stage = (Stage) Stage.getWindows().filtered(window -> window.isShowing()).get(0);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/frontend/login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 350, 400);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}