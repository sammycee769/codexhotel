package com.sammy.codexhotel.exceptions;

/**
 * A room's status cannot be changed because an active reservation holds it. Guards the status
 * endpoints so the flag can never be made to contradict the reservations.
 */
public class RoomUnavailableException extends RuntimeException {
    public RoomUnavailableException(String message) { super(message); }
}
