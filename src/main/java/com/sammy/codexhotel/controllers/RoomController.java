package com.sammy.codexhotel.controllers;

import com.sammy.codexhotel.data.models.RoomType;
import com.sammy.codexhotel.dtos.requests.AddRoomRequest;
import com.sammy.codexhotel.dtos.requests.UpdateRoomRequest;
import com.sammy.codexhotel.dtos.requests.UpdateUserRequest;
import com.sammy.codexhotel.dtos.responses.ApiResponse;
import com.sammy.codexhotel.dtos.responses.RoomResponse;
import com.sammy.codexhotel.exceptions.RoomAlreadyExistsException;
import com.sammy.codexhotel.exceptions.RoomNotFoundException;
import com.sammy.codexhotel.services.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomService roomService;

    @GetMapping("/available")
    public ResponseEntity<ApiResponse> getAvailableRooms(){
        List<RoomResponse>rooms = roomService.getAllAvailableRooms();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse(true, "Rooms available", rooms));
    }

    @GetMapping("/available/{roomType}")
    public ResponseEntity<ApiResponse> getAvailableRoomsByRoomType(@PathVariable RoomType roomType){
        List<RoomResponse>rooms = roomService.getAvailableRoomsByType(roomType);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse(true, "Rooms available", rooms));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllRooms(){
        List<RoomResponse>rooms = roomService.getRooms();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse(true, "Rooms available", rooms));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addRoom(@Valid @RequestBody AddRoomRequest roomRequest){
        try{
            RoomResponse roomResponse = roomService.addRoom(roomRequest);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Room added", roomResponse));
        }catch (RoomAlreadyExistsException e){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(),null));
        }
    }

    @PatchMapping("/maintenance/{roomId}")
    public ResponseEntity<ApiResponse> markRoomUnderMaintenance(@PathVariable String roomId){
        try {
            RoomResponse roomResponse = roomService.markAsMaintenance(roomId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ApiResponse(true, "Room under maintenance", roomResponse));
        }catch (RoomNotFoundException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage(),null));
        }
    }

    @PatchMapping("/{roomId}")
    public ResponseEntity<ApiResponse> updateRoom(@PathVariable String roomId, @Valid @RequestBody UpdateRoomRequest updateRoomRequest){
        try {
            RoomResponse roomResponse = roomService.updateRoom(updateRoomRequest,roomId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ApiResponse(true, "Room updated", roomResponse));
        }catch (RoomNotFoundException | RoomAlreadyExistsException e){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(),null));
        }
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<ApiResponse> deleteRoom(@PathVariable String roomId){
        try {
            roomService.deleteRoom(roomId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ApiResponse(true, "Room deleted", null));
        }catch (RoomNotFoundException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage(),null));
        }
    }

    @PatchMapping("/available/{roomId}")
    public ResponseEntity<ApiResponse> markAsAvailable(@PathVariable String roomId){
        try {
            RoomResponse roomResponse = roomService.markAsAvailable(roomId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ApiResponse(true, "Room available", roomResponse));
        }catch (RoomNotFoundException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage(),null));
        }
    }
}
