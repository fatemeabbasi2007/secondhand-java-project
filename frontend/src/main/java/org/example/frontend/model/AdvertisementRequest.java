package org.example.frontend.model;

import java.util.List;

public class AdvertisementRequest {
    private String  id;
    private String title;
    private String description;
    private double price;
    private String city;
    private String category;
    private List<String> imageUrlsList;

    // سازنده خالی الزامی برای Jackson
    public AdvertisementRequest() {}

    public AdvertisementRequest(String title, String description, double price, String city, String category) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.city = city;
        this.category = category;

    }

    public AdvertisementRequest(String id, String title, String description, double price, String city, String category, List<String> imageUrlsList) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.city = city;
        this.category = category;
        this.imageUrlsList = imageUrlsList;
    }

    // متدهای Getter و Setter
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

    public List<String> getImageUrlsList() {return imageUrlsList;}
    public void setImageUrlsList(List<String> imageUrlsList) {this.imageUrlsList = imageUrlsList;}

    public void setId(String id) {this.id = id;}
    public String getId() {return id;}
}