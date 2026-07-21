package org.example.frontend.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConversationResponse {

    private String conversationId;
    private String advertisementId;

    @JsonProperty("title") // نگاشت صحیح کلید title بک‌اند
    private String title;

    @JsonProperty("otherPartyName")
    @JsonAlias({"otherPartyName", "otherPartyUsername"})
    private String otherPartyUsername;

    private String lastMessagePreview;

    @JsonProperty("lastMessageAt")
    private String lastMessageAt;

    public ConversationResponse() {}

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    public String getAdvertisementId() { return advertisementId; }
    public void setAdvertisementId(String advertisementId) { this.advertisementId = advertisementId; }

    public String getAdvertisementTitle() { return title; }
    public void setAdvertisementTitle(String title) { this.title = title; }

    public String getOtherPartyUsername() { return otherPartyUsername; }
    public void setOtherPartyUsername(String otherPartyUsername) { this.otherPartyUsername = otherPartyUsername; }

    public String getLastMessagePreview() { return lastMessagePreview; }
    public void setLastMessagePreview(String lastMessagePreview) { this.lastMessagePreview = lastMessagePreview; }

    public String getLastMessageTime() { return lastMessageAt; }
    public void setLastMessageTime(String lastMessageAt) { this.lastMessageAt = lastMessageAt; }
}