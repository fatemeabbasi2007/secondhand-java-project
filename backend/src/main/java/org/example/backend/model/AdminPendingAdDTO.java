package org.example.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AdminPendingAdDTO {
    private String id;
    private String title;
    private String description;
    private double price;

    @JsonProperty("city")
    private String cityName;

    @JsonProperty("category")
    private String categoryId;

    private String ownerId;
    private String ownerUsername;

    @JsonProperty("imageUrl")
    private List<String> imageUrls; // تغییر نام به جمع جهت شفافیت بیشتر

    public AdminPendingAdDTO(Advertisement ad, String ownerUsername ) {
        this.id = ad.getId();
        this.title = ad.getTitle();
        this.description = ad.getDescription();
        this.price = ad.getPrice();
        this.imageUrls = ad.getImageUrls();
        this.cityName = ad.getCity();
        this.categoryId = ad.getCategoryId();
        this.ownerId = ad.getOwnerId();
        this.ownerUsername =ownerUsername;

    }
}