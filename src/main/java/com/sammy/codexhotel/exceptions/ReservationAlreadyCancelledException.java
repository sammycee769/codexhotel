package com.sammy.codexhotel.exceptions;

public class ReservationAlreadyCancelledException extends RuntimeException {
    public ReservationAlreadyCancelledException(String message) {
        super(message);
    }
}
