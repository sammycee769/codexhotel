package com.sammy.codexhotel.dtos.responses;

import com.sammy.codexhotel.data.models.RoomType;
import lombok.Data;

import java.time.LocalDate;
@Data
public class BookingResponse {
    private String bookingReference;
    private String guestName;
    private String email;
    private String phoneNumber;
    private int roomNumber;
    private RoomType roomType;
    private double pricePerNight;
    private double totalPayment;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String status;
}
