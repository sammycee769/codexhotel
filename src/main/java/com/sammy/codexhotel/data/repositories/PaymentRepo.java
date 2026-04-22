package com.sammy.codexhotel.data.repositories;

import com.sammy.codexhotel.data.models.Payment;
import com.sammy.codexhotel.data.models.RoomType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepo extends MongoRepository<Payment, String> {
    Optional<Payment> findByReservationId(String reservationId);
    List<Payment> findByRoomType(RoomType roomType);

}
