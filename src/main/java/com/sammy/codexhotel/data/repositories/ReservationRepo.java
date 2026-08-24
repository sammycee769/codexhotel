package com.sammy.codexhotel.data.repositories;

import com.sammy.codexhotel.data.models.Reservation;
import com.sammy.codexhotel.data.models.ReservationStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReservationRepo extends MongoRepository<Reservation, String> {
    List<Reservation> findByUserId(String userId);
    List<Reservation> findByRoomId(String roomId);
    Optional<Reservation> findByBookingReference(String bookingReference);
    List<Reservation> findByReservationStatus(ReservationStatus status);
    /**
     * Every reservation currently holding a room, in one round trip. Used by RoomService to work
     * out which rooms are actually sellable rather than trusting Room.roomStatus.
     */
    List<Reservation> findByReservationStatusIn(Collection<ReservationStatus> statuses);
    List<Reservation> findByReservationStatusAndCheckOutDateLessThanEqual(
            ReservationStatus status, LocalDate date
    );
    List<Reservation> findByReservationStatusAndCheckInDate(
            ReservationStatus status, LocalDate date
    );
}
