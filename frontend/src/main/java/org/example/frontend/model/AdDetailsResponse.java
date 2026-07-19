package org.example.frontend.model;

public class AdDetailsResponse {
    private Long id;
    private String title;
    private String description;
    private double price;
    private String city;
    private String category;
    private Long ownerId;
    private String ownerUsername;
    private double ownerAverageRating; // میانگین امتیاز فروشنده (مرحله ۵)
    private String status; // وضعیت آگهی (ACTIVE, SOLD, PENDING و غیره)

    public AdDetailsResponse() {}

    public AdDetailsResponse(Long id, String title, String description, double price, String city,
                             String category, Long ownerId, String ownerUsername, double ownerAverageRating, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.city = city;
        this.category = category;
        this.ownerId = ownerId;
        this.ownerUsername = ownerUsername;
        this.ownerAverageRating = ownerAverageRating;
        this.status = status;
    }

    // متدهای Getter و Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public String getOwnerUsername() { return ownerUsername; }
    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }

    public double getOwnerAverageRating() { return ownerAverageRating; }
    public void setOwnerAverageRating(double ownerAverageRating) { this.ownerAverageRating = ownerAverageRating; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}