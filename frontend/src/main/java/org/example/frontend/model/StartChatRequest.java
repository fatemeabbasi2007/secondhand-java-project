package org.example.frontend.model;

public class StartChatRequest {
    private Long advertisementId;
    private String firstMessage;

    public StartChatRequest() {}

    public StartChatRequest(Long advertisementId, String firstMessage) {
        this.advertisementId = advertisementId;
        this.firstMessage = firstMessage;
    }

    public Long getAdvertisementId() { return advertisementId; }
    public void setAdvertisementId(Long advertisementId) { this.advertisementId = advertisementId; }

    public String getFirstMessage() { return firstMessage; }
    public void setFirstMessage(String firstMessage) { this.firstMessage = firstMessage; }
}