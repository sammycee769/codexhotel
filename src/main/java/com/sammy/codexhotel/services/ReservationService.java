package com.sammy.codexhotel.services;

import com.sammy.codexhotel.data.models.Reservation;
import com.sammy.codexhotel.data.models.ReservationStatus;
import com.sammy.codexhotel.data.models.Room;
import com.sammy.codexhotel.data.models.User;
import com.sammy.codexhotel.data.repositories.ReservationRepo;
import com.sammy.codexhotel.dtos.requests.BookingRequest;
import com.sammy.codexhotel.dtos.responses.BookingResponse;
import com.sammy.codexhotel.dtos.responses.RoomResponse;
import com.sammy.codexhotel.exceptions.CannotCancelReservationException;
import com.sammy.codexhotel.exceptions.ReservationAlreadyCancelledException;
import com.sammy.codexhotel.exceptions.RoomNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.sammy.codexhotel.utils.Mappers.map;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepo reservationRepository;
    private final RoomService roomService;
    private final UserService userService;
    private final PricingService pricingService;
    private final NotificationService notificationService;

    public BookingResponse bookRoom(BookingRequest request){
        User user = userService.findUserById(request.getUserId());
        List<Room> availableRooms = new ArrayList<>();

        for (RoomResponse roomResponse : roomService.getAvailableRoomsByType(request.getRoomType())) {
            availableRooms.add(roomService.getRoomEntityById(roomResponse.getRoomId()));
        }
        validateRoomIsAvailable(request, availableRooms);
        Room room = availableRooms.get(0);
        double total = pricingService.calculateTotal(
                room.getRoomType(),
                request.getNumberOfNights(),
                request.isFestivePeriod()
        );
        Reservation reservation = new Reservation();
        reservation.setUserId(user.getUserId());
        reservation.setRoomId(room.getRoomId());
        reservation.setCheckInDate(request.getCheckInDate());
        reservation.setCheckInDate(request.getCheckInDate().plusDays(request.getNumberOfNights()));
        reservation.setNumberOfNights(request.getNumberOfNights());
        reservation.setTotalPayment(total);
        reservation.setBookingReference("RESERVE"+ UUID.randomUUID().toString().substring(0,6).toUpperCase());
        reservation.setReservationStatus(ReservationStatus.COMPLETED);

        roomService.markAsOccupied(room.getRoomId());
        Reservation saved = reservationRepository.save(reservation);
        notificationService.sendBookingConfirmation(user,saved,room);

        return map(user, room, reservation);
    }

    public BookingResponse cancelReservation(String reservationId){
        Reservation reservation = findReservationById(reservationId);
        checkReservationStatus(reservation);
        checkIfReservationisComplete(reservation);
        reservation.setReservationStatus(ReservationStatus.CANCELLED);
        roomService.markRoomAvailable(reservation.getRoomId());

        Reservation saved = reservationRepository.save(reservation);
        User user = userService.findUserById(saved.getUserId());
        Room room = roomService.getRoomEntityById(saved.getRoomId());

        return map(user, room, reservation);
    }

    public BookingResponse completeReservation(String reservationId){
        Reservation reservation = findReservationById(reservationId);
        checkReservationIsConfirmed(reservation);

        reservation.setReservationStatus(ReservationStatus.COMPLETED);
        roomService.markRoomAvailable(reservation.getRoomId());

        Reservation saved = reservationRepository.save(reservation);
        User user = userService.findUserById(saved.getUserId());
        Room room = roomService.getRoomEntityById(saved.getRoomId());
        return map(user, room, reservation);
    }

    public List<BookingResponse> getUserReservations(String userId) {
        List<BookingResponse> responses = new ArrayList<>();

        List<Reservation> reservations = reservationRepository.findByUserId(userId);
        User user = userService.findUserById(userId);

        for (Reservation reservation : reservations) {
            Room room = roomService.getRoomEntityById(reservation.getRoomId());

            responses.add(map(user,room,reservation));
        }

        return responses;
    }

    public List<BookingResponse> getAllReservations() {
        List<BookingResponse> responses = new ArrayList<>();

        List<Reservation> reservations = reservationRepository.findAll();

        for (Reservation reservation : reservations) {
            User user = userService.findUserById(reservation.getUserId());
            Room room = roomService.getRoomEntityById(reservation.getRoomId());

            responses.add(map(user, room,reservation));
        }

        return responses;
    }

    private static void checkReservationIsConfirmed(Reservation reservation) {
        if (reservation.getReservationStatus() != ReservationStatus.CONFIRMED) {
            throw new CannotCancelReservationException("Only confirmed reservations can be completed");
        }
    }

    private static void checkIfReservationisComplete(Reservation reservation) {
        if (reservation.getReservationStatus() == ReservationStatus.COMPLETED) {
            throw new CannotCancelReservationException("Cannot cancel a completed reservation");
        }
    }

    private static void checkReservationStatus(Reservation reservation) {
        if (reservation.getReservationStatus() == ReservationStatus.CANCELLED) {
            throw new ReservationAlreadyCancelledException("Reservation is already cancelled");
        }
    }

    private Reservation findReservationById(String reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + reservationId));
    }

    private static void validateRoomIsAvailable(BookingRequest request, List<Room> availableRooms) {
        if (availableRooms.isEmpty()){
            throw new RoomNotFoundException("No available rooms of type: "+ request.getRoomType());
        }
    }

}
