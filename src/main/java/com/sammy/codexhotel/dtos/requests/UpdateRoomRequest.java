package com.sammy.codexhotel.dtos.requests;

import com.sammy.codexhotel.data.models.RoomType;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdateRoomRequest {
    @Positive
    private Integer roomNumber;
    private RoomType roomType;
    @Positive
    private Double pricePerNight;
}
