package com.sammy.codexhotel.dtos.requests;

import com.sammy.codexhotel.data.models.RoomType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequest {

    @NotNull(message = "User ID is required")
    private String userId;

    @NotNull(message = "Room type is required")
    private RoomType roomType;

    @NotNull(message = "Check-in date is required")
    @Future(message = "Check-in date must be in the future")
    private LocalDate checkInDate;

    @Positive(message = "Number of nights must be greater than zero")
    private int numberOfNights;

    private boolean isFestivePeriod;
}