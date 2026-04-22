package com.sammy.codexhotel.dtos.requests;


import com.sammy.codexhotel.data.models.RoomType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotNull(message = "Room type is required")
    private RoomType roomType;
    @Positive(message = "Number of nights must be greater than zero")
    private int numberOfNights;
    private boolean isFestivePeriod;
}
