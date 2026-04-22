package com.sammy.codexhotel.data.models;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Document(collection = "reservations")
public class Reservation {
    @Id
    private String reservationId;
    private String bookingReference;
    private String userId;
    private String roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    @Positive(message = "Number Of Nights must be positive")
    private int numberOfNights;
    @Positive(message = "amount must be positive")
    private double totalPayment;
    private ReservationStatus reservationStatus = ReservationStatus.PENDING;
    private LocalDateTime createdAt = LocalDateTime.now();

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
