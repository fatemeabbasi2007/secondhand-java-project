package org.example.frontend.model;

public class MessageResponse {
    private Long id;
    private String senderUsername;
    private String content;
    private String timestamp;

    public MessageResponse() {}

    public MessageResponse(Long id, String senderUsername, String content, String timestamp) {
        this.id = id;
        this.senderUsername = senderUsername;
        this.content = content;
        this.timestamp = timestamp;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSenderUsername() { return senderUsername; }
    public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}