package org.example.backend.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id;               // Unique UUID string
    private String username;
    private String password;
    private String email;

    @JsonProperty("phoneNum")
    @JsonAlias({"phone", "phoneNumber", "phone_num", "mobile"})
    private String phoneNum;

    @JsonProperty("fullName")
    @JsonAlias({"name", "fullName", "full_name"})
    private String fullName;

    //  "USER" or "ADMIN"
    private String role;

    // for being blocked by admin
    private boolean enabled = true;

    // IDs of favorited ads
    private List<String> favoriteAdIds = new ArrayList<>();

    // ratings
    private double averageRating = 0.0;
    private int totalRatingsCount = 0;
    public void addFavoriteToList(String id){
        favoriteAdIds.add(id);
    }

}
