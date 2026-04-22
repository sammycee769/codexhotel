package com.sammy.codexhotel.dtos.responses;

import com.sammy.codexhotel.data.models.RoomStatus;
import com.sammy.codexhotel.data.models.RoomType;
import lombok.Data;

@Data
public class RoomResponse {
    private String roomId;
    private int roomNumber;
    private RoomType roomType;
    private RoomStatus roomStatus;
    private double pricePerNight;
}
