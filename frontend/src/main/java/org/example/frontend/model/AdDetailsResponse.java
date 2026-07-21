package org.example.frontend.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AdDetailsResponse {

    private String id;
    private String title;
    private String description;
    private double price;

    @JsonProperty("ownerId")
    @JsonAlias({"ownerId", "sellerId", "userId"})
    private String ownerId;

    @JsonAlias({"cityName", "city"})
    private String city;

    private String categoryId;

    @JsonProperty("ownerUsername")
    @JsonAlias({"ownerName", "ownerUsername", "sellerUsername"})
    private String ownerUsername;

    @JsonProperty("sellerRating")
    @JsonAlias({"sellerRating", "rating", "ownerRating", "ownerAverageRating"})
    private Double sellerRating;

    @JsonProperty("imageUrlsList")
    @JsonAlias({"imageUrls", "imageUrl", "imageUrlsList"})
    private List<String> imageUrlsList;

    private String status;

    private String AttributesJson;

    public AdDetailsResponse() {}

    // --- Getter ها و Setter ها همراه با کنترل Null ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description != null ? description : ""; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public String getCity() { return city != null ? city : "-"; }
    public void setCity(String city) { this.city = city; }

    public String getCategoryId() { return categoryId; }

    // Setter هوشمند: تمامی کلیدهای احتمالی دسته‌بندی را پوشش می‌دهد و مقادیر null را نادیده می‌گیرد
    @JsonAlias({"categoryId", "categoryName", "category", "category_name", "category_id"})
    public void setCategoryId(String categoryId) {
        if (categoryId != null && !categoryId.trim().isEmpty()) {
            this.categoryId = categoryId;
        }
    }

    public String getCategory() {
        return toCategoryName(categoryId);
    }

    public String getOwnerUsername() {
        return (ownerUsername != null && !ownerUsername.trim().isEmpty()) ? ownerUsername : "نامشخص";
    }
    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }

    public Double getSellerRating() { return sellerRating != null ? sellerRating : 0.0; }
    public void setSellerRating(Double sellerRating) { this.sellerRating = sellerRating; }

    public Double getOwnerAverageRating() {
        return getSellerRating();
    }

    public List<String> getImageUrlsList() { return imageUrlsList; }
    public void setImageUrlsList(List<String> imageUrlsList) { this.imageUrlsList = imageUrlsList; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAttributesJson() {return AttributesJson;}
    public void setAttributesJson(String attributesJson) {AttributesJson = attributesJson;}

    // تبدیل کد انگلیسی یا مقدار دریافت شده به نام فارسی مناسب
    private String toCategoryName(String cat) {
        if (cat == null || cat.trim().isEmpty()) {
            return "سایر";
        }
        String trimmed = cat.trim();
        switch (trimmed.toUpperCase()) {
            case "VEHICLES":
            case "CARS":
            case "CAR":
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
                // اگر مقدار از قبل فارسی بود (مثل "خودرو") دقیقاً همان را بازمی‌گرداند
                return trimmed;
        }
    }
}