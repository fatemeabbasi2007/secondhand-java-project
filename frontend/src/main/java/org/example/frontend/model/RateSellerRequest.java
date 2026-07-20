package org.example.frontend.model;

public class RateSellerRequest {
    private String advertisementId;
    private int rating; // عدد بین 1 تا 5
    private String comment; // نظر متنی اختیاری

    public RateSellerRequest() {}

    public RateSellerRequest(String advertisementId, int rating, String comment) {
        this.advertisementId = advertisementId;
        this.rating = rating;
        this.comment = comment;
    }

    public String getAdvertisementId() { return advertisementId; }
    public void setAdvertisementId(String advertisementId) { this.advertisementId = advertisementId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}