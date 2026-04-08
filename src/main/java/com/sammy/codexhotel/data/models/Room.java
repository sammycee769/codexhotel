package com.sammy.codexhotel.data.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "rooms")
public class Room {
    @Id
    private String roomId;
    @NotBlank(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private int roomNumber;
    @Positive(message = "Amount must be greater than zero")
    private double pricePerNight;
    private RoomType roomType;
    private RoomStatus roomStatus;
    private LocalDateTime createdAt = LocalDateTime.now();
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
