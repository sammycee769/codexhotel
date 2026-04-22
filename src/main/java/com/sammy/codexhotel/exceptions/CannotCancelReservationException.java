package com.sammy.codexhotel.exceptions;

public class CannotCancelReservationException extends RuntimeException {
    public CannotCancelReservationException(String message) {
        super(message);
    }
}
