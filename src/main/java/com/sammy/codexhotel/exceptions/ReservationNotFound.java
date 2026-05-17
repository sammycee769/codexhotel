package com.sammy.codexhotel.exceptions;

public class ReservationNotFound extends RuntimeException {
    public ReservationNotFound(String message) {
        super(message);
    }
}
