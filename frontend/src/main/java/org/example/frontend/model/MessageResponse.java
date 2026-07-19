package org.example.frontend.model;

import java.time.LocalDateTime;

public class MessageResponse {
    //private Long id;
    private String senderId;
    private String content;
    private LocalDateTime sendAt;

    public MessageResponse() {}

    public MessageResponse(String senderUsername, String content, LocalDateTime timestamp) {
        //this.id = id;
        this.senderId = senderUsername;
        this.content = content;
        this.sendAt = timestamp;
    }

    //public Long getId() { return id; }
    //public void setId(Long id) { this.id = id; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getSendAt() { return sendAt; }
    public void setSendAt(LocalDateTime sendAt) { this.sendAt = sendAt; }
}