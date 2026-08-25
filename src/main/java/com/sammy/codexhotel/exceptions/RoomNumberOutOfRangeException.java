package com.sammy.codexhotel.exceptions;

/**
 * A room number falls outside the band its category is numbered in. See RoomNumbering.
 */
public class RoomNumberOutOfRangeException extends RuntimeException {
    public RoomNumberOutOfRangeException(String message) { super(message); }
}
