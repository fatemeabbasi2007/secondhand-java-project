package org.example.frontend.model;

public class ChatResponse {
    private String conversationId;
    private String advertisementId;
    private String advertisementTitle;
    private String otherPartyUsername;
    private String lastMessage;
    private String lastMessageTime;

    public ChatResponse() {}

    public ChatResponse(String conversationId, String advertisementId, String advertisementTitle, String otherPartyUsername, String lastMessage, String lastMessageTime) {
        this.conversationId = conversationId;
        this.advertisementId = advertisementId;
        this.advertisementTitle = advertisementTitle;
        this.otherPartyUsername = otherPartyUsername;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
    }

    // متدهای Getter و Setter
    public String  getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    public String getAdvertisementId() { return advertisementId; }
    public void setAdvertisementId(String advertisementId) { this.advertisementId = advertisementId; }

    public String getAdvertisementTitle() { return advertisementTitle; }
    public void setAdvertisementTitle(String advertisementTitle) { this.advertisementTitle = advertisementTitle; }

    public String getOtherPartyUsername() { return otherPartyUsername; }
    public void setOtherPartyUsername(String otherPartyUsername) { this.otherPartyUsername = otherPartyUsername; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public String getLastMessageTime() { return lastMessageTime; }
    public void setLastMessageTime(String lastMessageTime) { this.lastMessageTime = lastMessageTime; }
}