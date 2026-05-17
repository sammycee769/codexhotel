package com.sammy.codexhotel.controllers;

import com.sammy.codexhotel.data.models.Reservation;
import com.sammy.codexhotel.dtos.requests.BookingRequest;
import com.sammy.codexhotel.dtos.responses.ApiResponse;
import com.sammy.codexhotel.dtos.responses.BookingResponse;
import com.sammy.codexhotel.exceptions.*;
import com.sammy.codexhotel.services.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping("/book")
    public ResponseEntity<ApiResponse> bookRoom(@Valid @RequestBody BookingRequest bookingRequest) {
        try {
            BookingResponse bookingResponse = reservationService.bookRoom(bookingRequest);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ApiResponse(true,"Room booked successfully",bookingResponse));
        }catch (UserNotFoundException | RoomNotFoundException e){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false,e.getMessage(),null));
        }
    }

    @PatchMapping("/cancel/{reservationId}")
    public ResponseEntity<ApiResponse> cancelReservation(@PathVariable String reservationId) {
        try {
            BookingResponse bookingResponse = reservationService.cancelReservation(reservationId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ApiResponse(true,"Room cancelled successfully",bookingResponse));
        }catch (UserNotFoundException | RoomNotFoundException | ReservationAlreadyCancelledException |
                CannotCancelReservationException e){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false,e.getMessage(),null));
        }
    }

    @PatchMapping("/complete/{reservationId}")
    public ResponseEntity<ApiResponse> completeReservation(@PathVariable String reservationId) {
        try {
            BookingResponse bookingResponse = reservationService.completeReservation(reservationId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ApiResponse(true,"Room completed successfully",bookingResponse));
        }catch (UserNotFoundException | RoomNotFoundException | ReservationNotFound | CannotCancelReservationException e){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false,e.getMessage(),null));
        }
    }

    @GetMapping("/my/{userId}")
    public ResponseEntity<ApiResponse> getUserReservations(@PathVariable String userId) {
        try {
            List<BookingResponse> bookingResponse = reservationService.getUserReservations(userId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ApiResponse(true,"Users reservations returned successfully",bookingResponse));
        }catch (UserNotFoundException | RoomNotFoundException e){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false,e.getMessage(),null));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllReservations() {
        List<BookingResponse> bookingResponse = reservationService.getAllReservations();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse(true,"Reservations returned successfully",bookingResponse));
    }
}
