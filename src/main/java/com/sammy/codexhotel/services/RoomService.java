package com.sammy.codexhotel.services;

import com.sammy.codexhotel.data.models.Reservation;
import com.sammy.codexhotel.data.models.ReservationStatus;
import com.sammy.codexhotel.data.models.Room;
import com.sammy.codexhotel.data.models.RoomStatus;
import com.sammy.codexhotel.data.models.RoomType;
import com.sammy.codexhotel.data.repositories.ReservationRepo;
import com.sammy.codexhotel.data.repositories.RoomRepo;
import com.sammy.codexhotel.dtos.requests.AddRoomRequest;
import com.sammy.codexhotel.dtos.requests.UpdateRoomRequest;
import com.sammy.codexhotel.dtos.responses.RoomNumberingResponse;
import com.sammy.codexhotel.dtos.responses.RoomResponse;
import com.sammy.codexhotel.exceptions.RoomAlreadyExistsException;
import com.sammy.codexhotel.exceptions.RoomNotFoundException;
import com.sammy.codexhotel.exceptions.RoomUnavailableException;
import com.sammy.codexhotel.utils.RoomNumbering;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sammy.codexhotel.utils.Mappers.map;

@Service
@RequiredArgsConstructor
public class RoomService {
    /** A room is held while a reservation with one of these statuses references it. */
    private static final Set<ReservationStatus> ACTIVE_STATUSES =
            Set.of(ReservationStatus.CONFIRMED, ReservationStatus.PENDING);

    private final RoomRepo roomRepository;
    // The repository interface, not ReservationService — so the dependency graph stays acyclic
    // (ReservationService -> RoomService -> ReservationRepo). Injecting ReservationService here
    // instead would close the loop; don't.
    private final ReservationRepo reservationRepository;

    public RoomResponse addRoom(AddRoomRequest request){
        RoomNumbering.validate(request.getRoomType(), request.getRoomNumber());
        validateIfRoomExists(request);
        Room room = new Room();
        map(request, room);
        roomRepository.save(room);
        return map(room);
    }

    public RoomResponse updateRoom(UpdateRoomRequest roomRequest, String roomId){
        Room room = getRoomEntityById(roomId);
        if (roomRequest.getRoomNumber() != null) {
            validateRoomNumberForUpdate(roomRequest.getRoomNumber(), roomId);
            room.setRoomNumber(roomRequest.getRoomNumber());
        }
        if (roomRequest.getRoomType() != null){
            room.setRoomType(roomRequest.getRoomType());
        }
        // Validate the effective type/number pair after applying the patch, not just the field that
        // arrived: a type-only PATCH turning room 105 from SINGLE into SUITE must be rejected just
        // as firmly as renumbering a suite to 105, and the request may carry either field alone.
        RoomNumbering.validate(room.getRoomType(), room.getRoomNumber());
        if (roomRequest.getPricePerNight() != null){
            room.setPricePerNight(roomRequest.getPricePerNight());
        }
        roomRepository.save(room);
        return map(room);
    }

    public List<RoomResponse> getRooms(){
        return roomRepository.findAll().stream().map(com.sammy.codexhotel.utils.Mappers::map).toList();
    }

    public void deleteRoom(String roomId) {
        Room room = getRoomEntityById(roomId);
        roomRepository.delete(room);
    }

    /**
     * Every sellable room, ordered by number. Sellable means not under maintenance and not held by
     * an active reservation — the roomStatus flag alone can no longer make a held room bookable.
     */
    public List<RoomResponse> getAllAvailableRooms(){
        Set<String> held = heldRoomIds();
        return roomRepository.findAll().stream()
                .filter(room -> isSellable(room, held))
                .sorted(Comparator.comparingInt(Room::getRoomNumber))
                .map(com.sammy.codexhotel.utils.Mappers::map)
                .toList();
    }

    /**
     * Sellable rooms of one category, lowest number first. bookRoom takes the head of this list,
     * so the ascending sort is what makes room assignment incremental (301, then 302, then 303...).
     */
    public List<RoomResponse> getAvailableRoomsByType(RoomType type){
        Set<String> held = heldRoomIds();
        return roomRepository.findByRoomType(type).stream()
                .filter(room -> isSellable(room, held))
                .sorted(Comparator.comparingInt(Room::getRoomNumber))
                .map(com.sammy.codexhotel.utils.Mappers::map)
                .toList();
    }

    /** Next free number plus the band it sits in, for the admin room form. */
    public RoomNumberingResponse numberingFor(RoomType type) {
        OptionalInt next = nextFreeNumber(type);
        return new RoomNumberingResponse(
                type,
                next.isPresent() ? next.getAsInt() : null,
                RoomNumbering.firstNumberInBand(type),
                RoomNumbering.bandLabel(type));
    }

    /**
     * Lowest unused number in the category's band, for the admin form's "next free" hint. Walks up
     * from the band's first room number past every number already taken. Returns the first gap, so
     * it fills holes left by deletions rather than only ever appending.
     *
     * <p>Empty when the band is full, which the caller has to render as "no number free" rather
     * than a figure: handing back the band's last number instead would prefill the form with a
     * number that is already taken. Only reachable for the bounded bands — the SUITE band has no
     * ceiling to run out of.
     */
    public OptionalInt nextFreeNumber(RoomType type) {
        Set<Integer> taken = roomRepository.findByRoomType(type).stream()
                .map(Room::getRoomNumber)
                .collect(Collectors.toSet());
        int last = RoomNumbering.lastNumberInBand(type);
        for (int candidate = RoomNumbering.firstRoomNumber(type); candidate <= last; candidate++) {
            if (!taken.contains(candidate)) return OptionalInt.of(candidate);
        }
        return OptionalInt.empty();
    }

    public RoomResponse markAsMaintenance(String roomId) {
        guardAgainstHeldRoom(roomId, "taken out of service");
        return updateStatus(roomId, RoomStatus.MAINTENANCE);
    }

    public RoomResponse markAsAvailable(String roomId) {
        // Closes the direct-API double-booking path: without this, PATCH /available on a held room
        // would flip the flag back to AVAILABLE and let a second guest be assigned the same room.
        guardAgainstHeldRoom(roomId, "marked available");
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
                .orElseThrow(() -> new RoomNotFoundException("Room not found with id: " + roomId));
    }

    /** roomId set of every room currently held by a CONFIRMED or PENDING reservation. */
    private Set<String> heldRoomIds() {
        return reservationRepository.findByReservationStatusIn(ACTIVE_STATUSES).stream()
                .map(Reservation::getRoomId)
                .collect(Collectors.toSet());
    }

    private boolean isSellable(Room room, Set<String> held) {
        return room.getRoomStatus() != RoomStatus.MAINTENANCE && !held.contains(room.getRoomId());
    }

    private void guardAgainstHeldRoom(String roomId, String action) {
        Room room = getRoomEntityById(roomId);
        if (heldRoomIds().contains(room.getRoomId())) {
            throw new RoomUnavailableException(
                    "Room " + room.getRoomNumber() + " has an active reservation and cannot be "
                            + action + " until the stay is completed or cancelled.");
        }
    }

    private void validateIfRoomExists(AddRoomRequest request) {
        if (roomRepository.findByRoomNumber(request.getRoomNumber()).isPresent()) {
            throw new RoomAlreadyExistsException("Room number already exists");
        }
    }

    private RoomResponse updateStatus(String roomId, RoomStatus status) {
        Room room = getRoomEntityById(roomId);
        room.setRoomStatus(status);
        roomRepository.save(room);
        return map(room);
    }

    private void validateRoomNumberForUpdate(Integer roomNumber, String roomId) {
        Room existingRoom = roomRepository.findByRoomNumber(roomNumber).orElse(null);

        if (existingRoom != null) {
            if (!existingRoom.getRoomId().equals(roomId)) {
                throw new RoomAlreadyExistsException("Room number already exists");
            }
        }
    }
}
