package org.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    private String id;          // Unique ID like "ELECTRONICS" or "LAPTOPS"
    private String name;        // Display name like "Electronics" or "Laptops"

    private String parentCategoryId;


}
