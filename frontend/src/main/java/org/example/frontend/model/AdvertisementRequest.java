package org.example.frontend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AdvertisementRequest {

    private String id;
    private String title;
    private String description;
    private double price;
    private String city;

    @JsonProperty("categoryId")
    private String categoryId;

    @JsonProperty("imageUrls")
    private List<String> imageUrlsList;

    private Map<String, String> specificAttributes;

//    private String AttributesJson;

    public AdvertisementRequest() {}

    public AdvertisementRequest(String title, String description, double price, String city, String categoryId, Map<String,String> attributesJson) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.city = city;
        this.categoryId = categoryId;
        this.specificAttributes= attributesJson;
    }

    public AdvertisementRequest(String id, String title, String description, double price, String city, String categoryId, List<String> imageUrlsList,Map<String,String>  attributesJson) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.city = city;
        this.categoryId = categoryId;
        this.imageUrlsList = imageUrlsList;
        this.specificAttributes = attributesJson;
    }

    // --- Getterها و Setterها ---
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCategory() { return categoryId; }
    public void setCategory(String categoryId) { this.categoryId = categoryId; }

    public List<String> getImageUrlsList() { return imageUrlsList; }
    public void setImageUrlsList(List<String> imageUrlsList) { this.imageUrlsList = imageUrlsList; }

    public void setId(String id) { this.id = id; }
    public String getId() { return id; }
    public Map<String,String> getSpecificAttributes() {
        return specificAttributes;
    }

    public void setSpecificAttributes(Map<String,String> specificAttributes) {
        this.specificAttributes = specificAttributes;
    }


}