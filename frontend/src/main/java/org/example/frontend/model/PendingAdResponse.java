package org.example.frontend.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true) // جلوگیری از کرش برنامه‌ در صورت وجود فیلدهای اضافی
public class PendingAdResponse {

    private String id;
    private String title;
    private String description;
    private double price;

    @JsonAlias({"cityName", "city"})
    private String city;

    @JsonAlias({"categoryId", "category"})
    private String category;

    @JsonProperty("ownerId")
    @JsonAlias({"ownerUsername", "ownerId"}) // هماهنگی ownerId بک‌اند با ownerUsername
    private String ownerUsername;

    @JsonProperty("imageUrl")
    @JsonAlias({"imageUrlsList", "imageUrls", "imageUrl"}) // هماهنگی imageUrl بک‌اند با imageUrlsList
    private List<String> imageUrlsList;

    public PendingAdResponse() {}

    public PendingAdResponse(String id, String title, String description, double price, String city, String category, String ownerUsername) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.city = city;
        this.category = category;
        this.ownerUsername = ownerUsername;
    }

    // --- Getterها و Setterها ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getOwnerUsername() { return ownerUsername; }
    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }

    public List<String> getImageUrlsList() { return imageUrlsList; }
    public void setImageUrlsList(List<String> imageUrlsList) { this.imageUrlsList = imageUrlsList; }
}