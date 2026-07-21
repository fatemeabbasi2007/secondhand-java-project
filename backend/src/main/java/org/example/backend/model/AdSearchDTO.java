package org.example.backend.model;

import lombok.Data;
import java.util.List;

@Data
public class AdSearchDTO {
    private String id;
    private String title;
    private String description;
    private double price;
    private String city;
    private String categoryId;
    private String ownerUsername;
    private List<String> imageUrls;

    public AdSearchDTO(Advertisement ad, String ownerUsername) {
        this.id = ad.getId();
        this.title = ad.getTitle();
        this.description = ad.getDescription();
        this.price = ad.getPrice();
        this.city = ad.getCity();
        this.categoryId = ad.getCategoryId();
        this.ownerUsername = ownerUsername;
        this.imageUrls = ad.getImageUrls();
    }
}