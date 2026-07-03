package org.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String senderId;       // User ID of who wrote the message
    private String content;        // The actual text payload
    private LocalDateTime sentAt = LocalDateTime.now(); //
}
