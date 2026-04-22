package com.sammy.codexhotel.dtos.responses;

import lombok.Data;

@Data
public class PaymentResponse {
    private String roomType;
    private double pricePerNight;
    private int numberOfNights;
    private boolean isFestivePeriod;
    private double surchargeAmount;
    private double totalPayment;
}
