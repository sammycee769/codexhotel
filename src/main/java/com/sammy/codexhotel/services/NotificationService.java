package com.sammy.codexhotel.services;

import com.sammy.codexhotel.data.models.*;
import com.sammy.codexhotel.data.repositories.NotificationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepo notificationRepository;

    public void sendBookingConfirmation(User user, Reservation reservation, Room room) {
        String message = String.format(
                "Booking confirmed! Dear %s, your booking reference is %s. " +
                        "Room %d (%s) is reserved from %s to %s. Total: ₦%.2f",
                user.getName(),
                reservation.getBookingReference(),
                room.getRoomNumber(),
                room.getRoomType(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                reservation.getTotalPayment()
        );
        saveNotification(user.getUserId(), reservation.getReservationId(), NotificationType.BOOKING_CONFIRMATION, message);
    }

    public void sendCheckInReminder(User user, Reservation reservation) {
        String message = String.format(
                "Reminder: Dear %s, your check-in at CodexHotel is tomorrow, %s. " +
                        "Booking reference: %s.",
                user.getName(),
                reservation.getCheckInDate(),
                reservation.getBookingReference()
        );
        saveNotification(user.getUserId(), reservation.getReservationId(), NotificationType.CHECKIN_REMINDER, message);
    }

    public void sendCancellationNotification(User user, Reservation reservation) {
        String message = String.format(
                "Your reservation %s has been cancelled. We hope to see you soon, %s.",
                reservation.getBookingReference(),
                user.getName()
        );
        saveNotification(user.getUserId(), reservation.getReservationId(), NotificationType.CANCELLATION, message);
    }

    public List<Notification> getUserNotifications(String userId) {
        return notificationRepository.findByUserId(userId);
    }
    private void saveNotification(String userId, String reservationId, NotificationType type, String message) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setReservationId(reservationId);
        notification.setType(type);
        notification.setMessage(message);
        notification.setSent(true);
        notification.setSentTime(LocalDateTime.now());
        notificationRepository.save(notification);
    }
}
