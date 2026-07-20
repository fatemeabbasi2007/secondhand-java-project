package org.example.frontend.model;

import java.time.LocalDateTime;

public class ConversationResponse {
    private String conversationId;
    private String advertisementId;
    private String title;
    private String otherPartyUsername;
    private String lastMessagePreview;
    private LocalDateTime lastMessageAt;

    public ConversationResponse() {}

    public ConversationResponse(String conversationId, String advertisementId, String advertisementTitle,
                                String otherPartyUsername, String lastMessage, LocalDateTime lastMessageTime) {
        this.conversationId = conversationId;
        this.advertisementId = advertisementId;
        this.title = advertisementTitle;
        this.otherPartyUsername = otherPartyUsername;
        this.lastMessagePreview = lastMessage;
        this.lastMessageAt = lastMessageTime;
    }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    public String getAdvertisementId() { return advertisementId; }
    public void setAdvertisementId(String advertisementId) { this.advertisementId = advertisementId; }

    public String getAdvertisementTitle() { return title; }
    public void setAdvertisementTitle(String advertisementTitle) { this.title = advertisementTitle; }

    public String getOtherPartyUsername() { return otherPartyUsername; }
    public void setOtherPartyUsername(String otherPartyUsername) { this.otherPartyUsername = otherPartyUsername; }

    public String getLastMessagePreview() { return lastMessagePreview; }
    public void setLastMessagePreview(String lastMessagePreview) { this.lastMessagePreview = lastMessagePreview; }

    public LocalDateTime getLastMessageTime() { return lastMessageAt; }
    public void setLastMessageTime(LocalDateTime lastMessageTime) { this.lastMessageAt = lastMessageTime; }
}