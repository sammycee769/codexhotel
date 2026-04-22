package com.sammy.codexhotel.dtos.requests;
import com.sammy.codexhotel.data.models.RoomType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AddRoomRequest {

    @NotNull(message = "Room number is required")
    @Positive(message = "Room number must be greater than zero")
    private int roomNumber;

    @NotNull(message = "Room type is required")
    private RoomType roomType;

    @Positive(message = "Price must be greater than zero")
    private double pricePerNight;
}