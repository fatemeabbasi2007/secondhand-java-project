package org.example.backend.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Data
public class Advertisement {
    private String id;               // Unique UUID string
    private String title;            // Searched by keyword
    private String description;      // Searched by keyword
    private double price;            // Filtered by min/max price, sortable
    private String city;             // Filtered by city

    @JsonAlias("category") // 👈 این خط را اضافه کنید
    private String categoryId;       // e.g., "ELECTRONICS", "VEHICLES"
    private String ownerId;          // Link to the User who created it
    private String rejectionReason;


    private AdStatus status = AdStatus.PENDING_REVIEW;
    private List<String> imageUrls = new ArrayList<>();
    // Timing for sorting
    private LocalDateTime createdAt = LocalDateTime.now();

    // MUST match a valid subcategory ID from your Category list (e.g., "LAPTOPS")

    private Map<String, String> specificAttributes = new HashMap<>();
    public void addRejectionReason(String s){
        this.rejectionReason = s;
    }

    public String getId() {
        return id;
    }
}
