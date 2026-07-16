module org.example.frontend {
    requires javafx.controls;
    requires javafx.fxml;

    // ۱. اضافه کردن دسترسی به کتابخانه جکسون و کلاینت شبکه جاوا
    requires java.net.http;
    requires com.fasterxml.jackson.databind;

    opens org.example.frontend to javafx.fxml;
    opens org.example.frontend.controller to javafx.fxml;

    // ۲. باز کردن پکیج مدل برای جکسون تا بتواند اطلاعات را به JSON تبدیل کند
    opens org.example.frontend.model to com.fasterxml.jackson.databind;

    exports org.example.frontend;
    exports org.example.frontend.controller;
    exports org.example.frontend.model;
}