package org.example.backend.model;

import java.time.LocalDateTime;

public class Review {

    //Unique Composite ID layout: "reviewerId_advertisementId"
    //This guarantees a user can only review a seller once per specific advertisement!
    private String id;

    private String reviewerId;       // The buyer giving the score
    private String sellerId;         // The seller receiving the score
    private String advertisementId;  // The specific transaction item

    private int score;               // Must be validated between 1 and 5
    private String comment;          // Optional text review
    private LocalDateTime createdAt = LocalDateTime.now();

    public String getId() {
        return id;
    }

    public String getReviewerId() {
        return reviewerId;
    }

    public String getAdvertisementId() {
        return advertisementId;
    }
}
