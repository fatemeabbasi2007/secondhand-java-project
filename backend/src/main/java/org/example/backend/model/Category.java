package org.example.backend.model;

public class Category {
    private String id;          // Unique ID like "ELECTRONICS" or "LAPTOPS"
    private String name;        // Display name like "Electronics" or "Laptops"

    private String parentCategoryId;

    public String getId() {
        return id;
    }
}
