package org.example.backend.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Conversation {
    private String id; //  "buyerId_advertisementId"
    private List<Message> messages = new ArrayList<>();


    private String advertisementId;
    private String sellerId;
    private String buyerId;

    private LocalDateTime lastMessageAt = LocalDateTime.now();
    private String lastMessagePreview = "";

    public String getId() {
        return id;
    }
}
