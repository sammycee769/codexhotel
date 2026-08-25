package com.sammy.codexhotel.exceptions;

/**
 * Every room in the requested category is held by an active reservation. Distinct from
 * RoomNotFoundException, which means a specific room id does not exist: this one says the hotel
 * is full for that category, so the controller answers 409 rather than 400.
 */
public class NoRoomsAvailableException extends RuntimeException {
    public NoRoomsAvailableException(String message) { super(message); }
}
