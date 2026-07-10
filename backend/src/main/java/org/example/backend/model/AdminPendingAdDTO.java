package org.example.backend.model;

import lombok.Data;

import java.util.List;
@Data
public class AdminPendingAdDTO {
    private String id;
    private String title;
    private String description;
    private double price;
    private String cityName;
    private String categoryId;
    private String ownerId;
    private List<String> imageUrl;
    public AdminPendingAdDTO(Advertisement ad) {
        this.id = ad.getId();
        this.title = ad.getTitle();
        this.description = ad.getDescription();
        this.price = ad.getPrice();
        this.imageUrl = ad.getImageUrls();
        this.cityName = ad.getCity();
        this.categoryId = ad.getCategoryId();
        this.ownerId = ad.getOwnerId();
    }

}
