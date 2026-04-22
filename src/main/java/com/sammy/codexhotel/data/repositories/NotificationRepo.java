package com.sammy.codexhotel.data.repositories;

import com.sammy.codexhotel.data.models.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepo extends MongoRepository<Notification, String> {
    List<Notification> findByUserId(String userId);
    List<Notification> findByIsSentFalse();
    List<Notification> findByReservationId(String reservationId);
}
