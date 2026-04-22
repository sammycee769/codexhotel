package com.sammy.codexhotel.services;

import com.sammy.codexhotel.data.models.Room;
import com.sammy.codexhotel.data.models.RoomStatus;
import com.sammy.codexhotel.data.models.RoomType;
import com.sammy.codexhotel.data.repositories.RoomRepo;
import com.sammy.codexhotel.dtos.requests.AddRoomRequest;
import com.sammy.codexhotel.dtos.responses.RoomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.sammy.codexhotel.utils.Mappers.map;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepo roomRepository;

    public RoomResponse addRoom(AddRoomRequest request){
        validateIfRoomExists(request);
        Room room = new Room();
        map(request, room);
        roomRepository.save(room);
        return map(room);
    }

    public List<RoomResponse> getRooms(){
        List<Room> rooms = roomRepository.findAll();
        List<RoomResponse> responses = new ArrayList<>();

        for (Room room : rooms) {
            responses.add(map(room));
        }
        return responses;
    }

    public List<RoomResponse> getAllAvailableRooms(){
        List<Room> rooms = roomRepository.findByRoomStatus(RoomStatus.AVAILABLE);
        List<RoomResponse> responses = new ArrayList<>();

        for (Room room : rooms) {
            responses.add(map(room));
        }
        return responses;
    }

    public List<RoomResponse> getAvailableRoomsByType(RoomType type){
        List<Room> rooms = roomRepository.findByRoomTypeAndRoomStatus(RoomStatus.AVAILABLE, type);
        List<RoomResponse> responses = new ArrayList<>();
        for (Room room : rooms) {
            responses.add(map(room));
        }
        return responses;
    }
    public RoomResponse markAsMaintenance(String roomId) {
        return updateStatus(roomId, RoomStatus.MAINTENANCE);
    }

    public RoomResponse markAsAvailable(String roomId) {
        return updateStatus(roomId, RoomStatus.AVAILABLE);
    }
    void markAsOccupied(String roomId) {
        updateStatus(roomId, RoomStatus.OCCUPIED);
    }
    void markRoomAvailable(String roomId) {
        updateStatus(roomId, RoomStatus.AVAILABLE);
    }

    Room getRoomEntityById(String roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + roomId));
    }

    private void validateIfRoomExists(AddRoomRequest request) {
        if (roomRepository.findByRoomNumber(request.getRoomNumber()).isPresent()) {
            throw new RuntimeException("Room number already exists");
        }
    }
    private RoomResponse updateStatus(String roomId, RoomStatus status) {
        Room room = getRoomEntityById(roomId);
        room.setRoomStatus(status);
        roomRepository.save(room);
        return map(room);
    }
}
