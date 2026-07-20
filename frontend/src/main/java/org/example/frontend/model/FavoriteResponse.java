package org.example.frontend.model;

public class FavoriteResponse {
    private String favoriteId; // شناسه ثبت علاقه‌مندی در دیتابیس
    private String advertisementId;
    private String title;
    private double price;
    private String city;

    public FavoriteResponse() {}

    public FavoriteResponse(String favoriteId, String advertisementId, String title, double price, String city) {
        this.favoriteId = favoriteId;
        this.advertisementId = advertisementId;
        this.title = title;
        this.price = price;
        this.city = city;
    }

    // متدهای Getter و Setter
    public String getFavoriteId() { return favoriteId; }
    public void setFavoriteId(String favoriteId) { this.favoriteId = favoriteId; }

    public String getAdvertisementId() { return advertisementId; }
    public void setAdvertisementId(String advertisementId) { this.advertisementId = advertisementId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
}