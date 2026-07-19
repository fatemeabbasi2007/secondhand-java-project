/*
package org.example.backend; // دقت کنید پکیج دقیقاً مطابق کلاس اصلی باشد

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// به جای لیست کردن دستی، کلاس اصلی را به عنوان رفرنس می‌دهیم.
// این کار هم سرور تامکت را لود می‌کند، هم تمام Service، Controller و Repository های شما را.
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = BackendApplication.class // 👈 نام کلاس اصلی پروژه خود را اینجا بنویسید
)
class FullIntegrationTest {

    @Test
    void keepServerRunning() {
        System.out.println("🚀 سرور با تمام لایه‌ها روی پورت 8080 (یا پورت تعریف شده در application.properties) بالا آمد.");
        System.out.println("🌐 اکنون می‌توانید با Postman ریکوئست بزنید...");
        System.out.println("⚠️ برای متوقف کردن سرور، تست را به صورت دستی Stop کنید.");

        try {
            // سرور تا زمانی که تست را متوقف نکنید، روشن می‌ماند
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

 */