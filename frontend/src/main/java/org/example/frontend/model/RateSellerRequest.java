package org.example.frontend.model;

public class RateSellerRequest {
    private Long advertisementId;
    private int rating; // عدد بین 1 تا 5
    private String comment; // نظر متنی اختیاری

    public RateSellerRequest() {}

    public RateSellerRequest(Long advertisementId, int rating, String comment) {
        this.advertisementId = advertisementId;
        this.rating = rating;
        this.comment = comment;
    }

    public Long getAdvertisementId() { return advertisementId; }
    public void setAdvertisementId(Long advertisementId) { this.advertisementId = advertisementId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}