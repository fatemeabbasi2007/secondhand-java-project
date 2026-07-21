package org.example.frontend.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true) // جلوگیری از کرش در صورت وجود فیلدهای اضافی
public class AdDetailsResponse {

    private String id;
    private String title;
    private String description;
    private double price;

    @JsonAlias({"cityName", "city"})
    private String city;
    private String categoryId;
    private Long ownerId;

    @JsonAlias({"categoryId", "categoryName", "category"})
    private String category;

    // اصلاح نوع داده به String برای همخوانی با بک‌اند
    private String ownerId;

    @JsonAlias({"ownerName", "ownerUsername"})
    private String ownerUsername;

    private double ownerAverageRating;
    private String status;

    @JsonProperty("imageUrlsList")
    @JsonAlias({"imageUrls", "imageUrl", "imageUrlsList"})
    private List<String> imageUrlsList;

    public AdDetailsResponse() {}

    public AdDetailsResponse(String id, String title, String description, double price, String city,
                             String category, String ownerId, String ownerUsername, double ownerAverageRating, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.city = city;
        this.categoryId = category;
        this.ownerId = ownerId;
        this.ownerUsername = ownerUsername;
        this.ownerAverageRating = ownerAverageRating;
        this.status = status;
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

    public String getCategory() { return toCategoryId(categoryId); }
    public void setCategory(String category) { this.categoryId = category; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public String getOwnerUsername() { return ownerUsername; }
    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }

    public double getOwnerAverageRating() { return ownerAverageRating; }
    public void setOwnerAverageRating(double ownerAverageRating) { this.ownerAverageRating = ownerAverageRating; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<String> getImageUrlsList() {return imageUrlsList;}
    public void setImageUrlsList(List<String> imageUrlsList) {this.imageUrlsList = imageUrlsList;}

    private String toCategoryId (String category) {
        switch (category.trim()) {

            case "VEHICLES":
                return "وسایل نقلیه";
            case "CARS":
                return "خودرو";
            case "MOTORCYCLES":
                return "موتورسیکلت";

            case "REAL_ESTATE":
                return "املاک";
            case "APARTMENTS":
                return "آپارتمان و مسکونی";
            case "COMMERCIAL":
                return "اداری و تجاری";

            case "ELECTRONICS":
                return "لوازم الکترونیکی";
            case "MOBILE_PHONES":
                return "موبایل و تبلت";
            case "LAPTOPS":
                return "لپ‌تاپ و کامپیوتر";

            case "HOME_GOODS":
                return "وسایل خانه و آشپزخانه";
            case "FURNITURE":
                return "مبلمان و لوازم چوبی";

            case "PERSONAL_ITEMS":
                return "وسایل شخصی";
            case "CLOTHING":
                return "پوشاک و کیف و کفش";

            default:
                return "";
        }
    }
    public List<String> getImageUrlsList() { return imageUrlsList; }
    public void setImageUrlsList(List<String> imageUrlsList) { this.imageUrlsList = imageUrlsList; }
}