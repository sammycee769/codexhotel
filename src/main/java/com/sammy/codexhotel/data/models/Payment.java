package com.sammy.codexhotel.data.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "payments")
public class Payment {
    @Id
    private String paymentId;
    private String reservationId;
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private double amount;
    @Positive(message = "Amount must be greater than zero")
    private double surchargePayment;
    private boolean isFestivePeriod;
    private RoomType roomType;
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private int numberOfNights;
}
