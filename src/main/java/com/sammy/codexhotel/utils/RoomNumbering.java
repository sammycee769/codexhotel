package com.sammy.codexhotel.utils;

import com.sammy.codexhotel.data.models.RoomType;
import com.sammy.codexhotel.exceptions.RoomNumberOutOfRangeException;

/**
 * Room numbers are banded by category, so a number says which category it belongs to:
 * Deluxe (SINGLE) 100-199, Junior (DOUBLE) 200-299, Executive (SUITE) 300 and above.
 *
 * <p>Kept out of the RoomType enum on purpose — that enum mirrors the frontend's string union
 * exactly and is better left a pure wire type with no policy attached.
 *
 * <p>The Executive band is open-ended: the top of the house should be able to grow without a code
 * change, and there is no category above it whose numbers it could collide with.
 */
public final class RoomNumbering {

    private RoomNumbering() {}

    /** Inclusive lower bound of the band. */
    public static int firstNumberInBand(RoomType type) {
        return switch (type) {
            case SINGLE -> 100;
            case DOUBLE -> 200;
            case SUITE -> 300;
        };
    }

    /** Inclusive upper bound of the band. */
    public static int lastNumberInBand(RoomType type) {
        return switch (type) {
            case SINGLE -> 199;
            case DOUBLE -> 299;
            case SUITE -> Integer.MAX_VALUE;
        };
    }

    /**
     * Where numbering actually starts, one above the band floor: hotels have a room 101, not a
     * room 100. The band itself still accepts the floor, so a hand-added room 100 is not rejected —
     * this is only the starting point for seeding and for the admin form's hint.
     */
    public static int firstRoomNumber(RoomType type) {
        return firstNumberInBand(type) + 1;
    }

    public static boolean isInBand(RoomType type, int roomNumber) {
        return roomNumber >= firstNumberInBand(type) && roomNumber <= lastNumberInBand(type);
    }

    public static void validate(RoomType type, int roomNumber) {
        if (!isInBand(type, roomNumber)) {
            throw new RoomNumberOutOfRangeException(
                    "Room " + roomNumber + " is outside the " + type + " range (" + bandLabel(type)
                            + "). Renumber the room or change its category.");
        }
    }

    /** Human-readable band, for error messages and the admin form's hint. */
    public static String bandLabel(RoomType type) {
        if (lastNumberInBand(type) == Integer.MAX_VALUE) {
            return firstNumberInBand(type) + " and above";
        }
        return firstNumberInBand(type) + "-" + lastNumberInBand(type);
    }
}
