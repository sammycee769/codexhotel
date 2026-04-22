package com.sammy.codexhotel.data.models;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "notifications")
public class Notification {
    private String userId;
    @Id
    private String notificationId;
    private String reservationId;
    @NotBlank(message = "Message is required")
    private String message;
    private NotificationType type;
    private boolean isSent;
    private LocalDateTime sentTime;
    private LocalDateTime createdAt = LocalDateTime.now();


}
