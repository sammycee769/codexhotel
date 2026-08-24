package com.sammy.codexhotel.dtos.responses;

import com.sammy.codexhotel.data.models.RoomType;
import lombok.Data;

import java.time.LocalDate;
@Data
public class BookingResponse {
    // The cancel and complete endpoints are keyed on reservationId, so it has to travel with the
    // response — the human-facing bookingReference cannot address those routes.
    private String reservationId;
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
