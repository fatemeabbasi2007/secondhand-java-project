module org.example.frontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;

    // اضافه کردن ماژول Jackson
    requires com.fasterxml.jackson.databind;

    opens org.example.frontend to javafx.fxml;
    opens org.example.frontend.controller to javafx.fxml;

    // اجازه دادن به جکسون برای خواندن کلاس‌های مدل
    opens org.example.frontend.model to com.fasterxml.jackson.databind;


    exports org.example.frontend;
    exports org.example.frontend.controller;
    exports org.example.frontend.model;
    exports org.example.frontend.util;
    exports org.example.frontend.service;
    exports org.example.frontend.security;
    exports org.example.frontend.config;
}