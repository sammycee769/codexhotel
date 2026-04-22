package com.sammy.codexhotel.data.repositories;

import com.sammy.codexhotel.data.models.Room;
import com.sammy.codexhotel.data.models.RoomStatus;
import com.sammy.codexhotel.data.models.RoomType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepo extends MongoRepository<Room, String> {
    List<Room> findByRoomStatus(RoomStatus roomStatus);
    List<Room> findByRoomType(RoomType roomType);
    Optional<Room> findByRoomNumber(int roomNumber);
    List<Room> findByRoomNumberAndRoomStatus(int roomNumber, RoomStatus roomStatus);
    List<Room> findByRoomTypeAndRoomStatus(RoomStatus status,  RoomType roomType);
}
