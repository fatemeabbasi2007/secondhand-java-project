package org.example.frontend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageResponse {

    private String senderId;
    private String content;

    @JsonProperty("sentAt") // نگاشت صحیح فیلد زمان بک‌اند
    private String sentAt;

    public MessageResponse() {}

    public MessageResponse(String senderId, String content, String sentAt) {
        this.senderId = senderId;
        this.content = content;
        this.sentAt = sentAt;
    }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getSendAt() { return sentAt; }
    public void setSendAt(String sentAt) { this.sentAt = sentAt; }
}