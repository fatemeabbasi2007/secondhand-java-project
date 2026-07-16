package org.example.frontend.model;

import java.time.LocalDateTime;

public class ChatMessage {
    private int senderId;
    private String senderName;
    private String text;
    private LocalDateTime timestamp;

    public ChatMessage(int senderId, String senderName, String text) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.text = text;
        this.timestamp = LocalDateTime.now();
    }

    public ChatMessage(){}

    // Getterها
    public int getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }
    public String getText() { return text; }
    public LocalDateTime getTimestamp() { return timestamp; }

    public void setSenderId(int senderId) {this.senderId = senderId;}
    public void setSenderName(String senderName) {this.senderName = senderName;}
    public void setText(String text) {this.text = text;}
    public void setTimestamp(LocalDateTime timestamp) {this.timestamp = timestamp;}
}
