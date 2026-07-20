package org.example.frontend.model;

public class StartChatRequest {
    private String senderId;
    private String content;

    public StartChatRequest() {}

    public StartChatRequest(String advertisementId, String firstMessage) {
        this.senderId = advertisementId;
        this.content = firstMessage;
    }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}