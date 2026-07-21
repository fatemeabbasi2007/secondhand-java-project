package org.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Data
//@AllArgsConstructor
@NoArgsConstructor
public class Conversation {
    private String id; //  "buyerId_advertisementId"
    private List<Message> messages = new ArrayList<>();


    private String advertisementId;
    private String sellerId;
    private String buyerId;

    public Conversation(String id ,String advertisementId , String sellerId , String buyerId) {
        this.id = id;
        this.advertisementId = advertisementId;
        this.sellerId = sellerId;
        this.buyerId = buyerId;
    }

    private LocalDateTime lastMessageAt = LocalDateTime.now();
    private String lastMessagePreview = "";
    public void addMessageToList(Message m) {
        if (this.messages == null) {
            this.messages = new ArrayList<>();
        }
        messages.add(m);
    }

}
