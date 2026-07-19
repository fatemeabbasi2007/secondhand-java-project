package org.example.frontend.model;

public class StartChatRequest {
    private Long senderId;
    private String content;

    public StartChatRequest() {}

    public StartChatRequest(Long advertisementId, String firstMessage) {
        this.senderId = advertisementId;
        this.content = firstMessage;
    }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}