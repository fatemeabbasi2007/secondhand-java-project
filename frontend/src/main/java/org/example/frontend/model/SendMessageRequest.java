package org.example.frontend.model;

public class SendMessageRequest {
    private String senderId;
    private String content;

    public SendMessageRequest() {}

    public SendMessageRequest(String content) {
        this.content = content;
    }

    public SendMessageRequest(String senderId, String content) {
        this.senderId = senderId;
        this.content = content;
    }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}