package org.example.frontend.model;

public class Advertisement {
    private int id;
    private String title;
    private String description;
    private double price;
    private int categoryId;
    private int cityId;
    private int sellerId;

    // فیلدهای جدید اضافه شده طبق سناریو
    private String sellerName;
    private double sellerRating;
    private String cityName;
    private String categoryName;
    private boolean isSold; // وضعیت فروخته شده بودن آگهی

    // فیلدهای جدید برای هماهنگی با سناریوی تایید ادمین
    private String status; // مقدارها: "PENDING", "APPROVED", "REJECTED"
    private String rejectReason; // دلیل رد آگهی توسط مدیر

    public Advertisement() {} // برای استفاده جکسون هنگام دریافت اطلاعات از سرور

    // سازنده (Constructor) به‌روزرسانی شده
    public Advertisement(int id, String title, String description, double price, int categoryId, int cityId, int sellerId,
                         String sellerName, double sellerRating, String cityName, String categoryName, boolean isSold, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.categoryId = categoryId;
        this.cityId = cityId;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.sellerRating = sellerRating;
        this.cityName = cityName;
        this.categoryName = categoryName;
        this.isSold = isSold;
        this.status = status; // مثل "PENDING" یا "APPROVED"
        this.rejectReason = "";
    }



    // گترها و سترها
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public int getCategoryId() { return categoryId; }
    public int getCityId() { return cityId; }
    public int getSellerId() { return sellerId; }
    public String getSellerName() { return sellerName; }
    public double getSellerRating() { return sellerRating; }
    public String getCityName() { return cityName; }
    public String getCategoryName() { return categoryName; }
    public boolean isSold() { return isSold; }
    public void setSold(boolean sold) { isSold = sold; }

    // گترها و سترهای جدید
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }

    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public void setCityId(int cityId) { this.cityId = cityId; }
    public void setSellerId(int sellerId) { this.sellerId = sellerId; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }
    public void setSellerRating(double sellerRating) { this.sellerRating = sellerRating; }
    public void setCityName(String cityName) { this.cityName = cityName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}