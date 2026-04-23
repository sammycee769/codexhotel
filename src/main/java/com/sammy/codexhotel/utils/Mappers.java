package com.sammy.codexhotel.utils;

import com.sammy.codexhotel.data.models.Reservation;
import com.sammy.codexhotel.data.models.Room;
import com.sammy.codexhotel.data.models.RoomStatus;
import com.sammy.codexhotel.data.models.User;
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
        user.setRole(registerUserRequest.getRole());
        return user;
    }

    public static RegisterUserResponse map(RegisterUserRequest registerUserRequest, User user) {
        RegisterUserResponse registerUserResponse = new RegisterUserResponse();
        registerUserResponse.setEmail(registerUserRequest.getEmail());
        registerUserResponse.setName(registerUserRequest.getName());
        registerUserResponse.setRole(registerUserRequest.getRole());
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

    public static void mapUpdate(User existingUser, UpdateUserRequest request) {
        existingUser.setName(request.getName());
        existingUser.setEmail(request.getEmail());
        existingUser.setPhoneNumber(request.getPhoneNumber());
    }

    public static PaymentResponse map(PaymentRequest paymentRequest, double basePrice, double total, double surchargeAmount) {
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setRoomType(paymentRequest.getRoomType().name());
        paymentResponse.setNumberOfNights(paymentRequest.getNumberOfNights());
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
        bookingResponse.setBookingReference(reservation.getBookingReference());
        bookingResponse.setGuestName(user.getName());
        bookingResponse.setEmail(user.getEmail());
        bookingResponse.setEmail(user.getEmail());
        bookingResponse.setPhoneNumber(user.getPhoneNumber());
        bookingResponse.setRoomNumber(room.getRoomNumber());
        bookingResponse.setRoomType(room.getRoomType());
        bookingResponse.setPricePerNight(room.getPricePerNight());
        bookingResponse.setTotalPayment(reservation.getTotalPayment());
        bookingResponse.setCheckInDate(reservation.getCheckInDate());
        bookingResponse.setCheckOutDate(reservation.getCheckInDate());
        bookingResponse.setStatus(reservation.getReservationStatus().toString());
        return bookingResponse;
    }
}
