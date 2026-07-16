package org.example.frontend.network;

import java.util.ArrayList;
import java.util.List;

public class MockAdDatabase {

    public static class Ad {
        private int id;
        private String title;
        private String category;
        private String city;
        private double price;
        private String description;
        private String imageName;
        private String status; // "PENDING", "APPROVED", "REJECTED"
        private String rejectReason; // دلیل رد آگهی (بخش آخر سناریو)

        public Ad(int id, String title, String category, String city, double price, String description, String imageName, String status) {
            this.id = id;
            this.title = title;
            this.category = category;
            this.city = city;
            this.price = price;
            this.description = description;
            this.imageName = imageName;
            this.status = status;
            this.rejectReason = "";
        }

        // Getter & Setter
        public int getId() { return id; }
        public String getTitle() { return title; }
        public String getCategory() { return category; }
        public String getCity() { return city; }
        public double getPrice() { return price; }
        public String getDescription() { return description; }
        public String getImageName() { return imageName; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getRejectReason() { return rejectReason; }
        public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
    }

    private static final List<Ad> allAds = new ArrayList<>();
    private static int nextId = 1;

    static {
        // چند آگهی پیش‌فرض برای تست پنل مدیریت
        allAds.add(new Ad(nextId++, "گوشی آیفون ۱۳ پرو", "الکترونیک و دیجیتال", "تهران", 45000000, "بسیار تمیز بدون خط و خش", "iphone.jpg", "PENDING"));
        allAds.add(new Ad(nextId++, "پراید مدل ۹۵", "وسایل نقلیه", "اصفهان", 220000000, "شاسی پلمپ، گلگیر رنگ", "car.jpg", "PENDING"));
        allAds.add(new Ad(nextId++, "کتاب جاوا برای مبتدیان", "کتاب و لوازم تحریر", "شیراز", 150000, "کاملا نو ورق نخورده", "book.jpg", "APPROVED")); // این فعال است و در لیست عمومی دیده می‌شود
    }

    public static List<Ad> getAllAds() {
        return allAds;
    }

    public static void addAd(Ad ad) {
        ad.id = nextId++;
        allAds.add(ad);
    }
}