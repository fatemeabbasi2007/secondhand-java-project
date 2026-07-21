package org.example.frontend.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true) // جلوگیری از کرش در صورت وجود فیلدهای اضافی در JSON
public class FavoriteResponse {

    @JsonProperty("favoriteId")
    @JsonAlias({"id", "favoriteId"}) // پشتیبانی هم‌زمان از کلید id و favoriteId
    private String favoriteId;

    private String advertisementId;
    private String title;
    private Double price;
    private String city;

    public FavoriteResponse() {}

    public FavoriteResponse(String favoriteId, String advertisementId, String title, Double price, String city) {
        this.favoriteId = favoriteId;
        this.advertisementId = advertisementId;
        this.title = title;
        this.price = price;
        this.city = city;
    }

    // --- Getterها و Setterها ---
    public String getFavoriteId() { return favoriteId; }
    public void setFavoriteId(String favoriteId) { this.favoriteId = favoriteId; }

    public String getAdvertisementId() { return advertisementId; }
    public void setAdvertisementId(String advertisementId) { this.advertisementId = advertisementId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
}