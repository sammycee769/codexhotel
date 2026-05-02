package com.sammy.codexhotel.scheduler;

import com.sammy.codexhotel.data.models.Reservation;
import com.sammy.codexhotel.data.models.ReservationStatus;
import com.sammy.codexhotel.data.repositories.ReservationRepo;
import com.sammy.codexhotel.services.NotificationService;
import com.sammy.codexhotel.services.RoomService;
import com.sammy.codexhotel.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationScheduler {
    private final ReservationRepo reservationRepository;
    private final RoomService roomService;
    private final NotificationService notificationService;
    private final UserService userService;

    @Scheduled(cron = " 0 0 0 * * *")
    public void autoCompleteExpiredReservations(){
        log.info("Running reservation checkout scheduler...");

        List<Reservation> expired = reservationRepository.findByReservationStatusAndCheckOutDateLessThanEqual(ReservationStatus.CONFIRMED, LocalDate.now());
        for (Reservation reservation : expired) {
            reservation.setReservationStatus(ReservationStatus.COMPLETED);
            reservationRepository.save(reservation);
            roomService.markAsAvailable(reservation.getRoomId());
            log.info("Auto-completed reservation: {}", reservation.getBookingReference());
        }
        log.info("Scheduler done. {} reservation(s) completed.", expired.size());
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void sendCheckInReminders() {
        log.info("Running check-in reminder scheduler...");

        LocalDate tomorrow = LocalDate.now().plusDays(1);

        List<Reservation> checkingInTomorrow = reservationRepository.findByReservationStatusAndCheckInDate(ReservationStatus.CONFIRMED, tomorrow);

        for (Reservation reservation : checkingInTomorrow) {
            notificationService.sendCheckInReminder(userService.findUserById(reservation.getUserId()), reservation);
            log.info("Reminder sent for reservation: {}", reservation.getBookingReference());
        }
    }
}
