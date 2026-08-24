package com.sammy.codexhotel.utils;

import com.sammy.codexhotel.data.models.Reservation;
import com.sammy.codexhotel.data.models.Room;
import com.sammy.codexhotel.data.models.RoomStatus;
import com.sammy.codexhotel.data.models.User;
import com.sammy.codexhotel.data.models.UserRole;
import com.sammy.codexhotel.dtos.requests.AddRoomRequest;
import com.sammy.codexhotel.dtos.requests.PaymentRequest;
import com.sammy.codexhotel.dtos.requests.RegisterUserRequest;
import com.sammy.codexhotel.dtos.requests.UpdateUserRequest;
import com.sammy.codexhotel.dtos.responses.*;

public class Mappers {

    public static User map(RegisterUserRequest registerUserRequest) {
        User user = new User();
        user.setEmail(registerUserRequest.getEmail());
        user.setName(registerUserRequest.getName());
        user.setPhoneNumber(registerUserRequest.getPhoneNumber());
        user.setRole(UserRole.GUEST);
        return user;
    }

    public static RegisterUserResponse map(RegisterUserRequest registerUserRequest, User user) {
        RegisterUserResponse registerUserResponse = new RegisterUserResponse();
        registerUserResponse.setEmail(registerUserRequest.getEmail());
        registerUserResponse.setName(registerUserRequest.getName());
        registerUserResponse.setRole(user.getRole());
        registerUserResponse.setUserId(user.getUserId());
        registerUserResponse.setPhoneNumber(registerUserRequest.getPhoneNumber());
        return registerUserResponse;
    }

    public static UserResponse mapToUser(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(user.getUserId());
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());
        userResponse.setRole(user.getRole());
        userResponse.setPhoneNumber(user.getPhoneNumber());
        return userResponse;
    }

    /**
     * PATCH semantics: only overwrite fields the caller actually sent. Assigning
     * unconditionally would null out any field omitted from the request body.
     */
    public static void mapUpdate(User existingUser, UpdateUserRequest request) {
        if (request.getName() != null) {
            existingUser.setName(request.getName());
        }
        if (request.getEmail() != null) {
            existingUser.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(request.getPhoneNumber());
        }
    }

    public static LoginResponse map(User user, String token, long expiresInMs) {
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token);
        loginResponse.setUserId(user.getUserId());
        loginResponse.setName(user.getName());
        loginResponse.setEmail(user.getEmail());
        loginResponse.setPhoneNumber(user.getPhoneNumber());
        loginResponse.setRole(user.getRole());
        loginResponse.setExpiresInMs(expiresInMs);
        return loginResponse;
    }

    public static PaymentResponse map(PaymentRequest paymentRequest, double basePrice, double total, double surchargeAmount) {
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setRoomType(paymentRequest.getRoomType().name());
        paymentResponse.setNumberOfNights(paymentRequest.getNumberOfNights());
        paymentResponse.setFestivePeriod(paymentRequest.isFestivePeriod());
        paymentResponse.setPricePerNight(basePrice);
        paymentResponse.setTotalPayment(total);
        paymentResponse.setSurchargeAmount(surchargeAmount);
        return paymentResponse;
    }
    public static void map(AddRoomRequest request, Room room) {
        room.setRoomNumber(request.getRoomNumber());
        room.setRoomType(request.getRoomType());
        room.setPricePerNight(request.getPricePerNight());
        room.setRoomStatus(RoomStatus.AVAILABLE);
    }
    public static RoomResponse map(Room room) {
        RoomResponse roomResponse = new RoomResponse();
        roomResponse.setRoomId(room.getRoomId());
        roomResponse.setRoomNumber(room.getRoomNumber());
        roomResponse.setRoomType(room.getRoomType());
        roomResponse.setPricePerNight(room.getPricePerNight());
        roomResponse.setRoomStatus(room.getRoomStatus());
        return roomResponse;
    }

    public static BookingResponse map( User user, Room room, Reservation reservation) {
        BookingResponse bookingResponse = new BookingResponse();
        bookingResponse.setReservationId(reservation.getReservationId());
        bookingResponse.setBookingReference(reservation.getBookingReference());
        bookingResponse.setGuestName(user.getName());
        bookingResponse.setEmail(user.getEmail());

        bookingResponse.setPhoneNumber(user.getPhoneNumber());
        bookingResponse.setRoomNumber(room.getRoomNumber());
        bookingResponse.setRoomType(room.getRoomType());
        bookingResponse.setPricePerNight(room.getPricePerNight());
        bookingResponse.setTotalPayment(reservation.getTotalPayment());
        bookingResponse.setCheckInDate(reservation.getCheckInDate());
        bookingResponse.setCheckOutDate(reservation.getCheckOutDate());
        bookingResponse.setStatus(reservation.getReservationStatus().toString());
        return bookingResponse;
    }
}
