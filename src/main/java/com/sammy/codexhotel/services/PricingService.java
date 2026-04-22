package com.sammy.codexhotel.services;

import com.sammy.codexhotel.data.models.RoomType;
import com.sammy.codexhotel.dtos.requests.PaymentRequest;
import com.sammy.codexhotel.dtos.responses.PaymentResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import static com.sammy.codexhotel.utils.Mappers.map;

@Service
public class PricingService {
    private static final double FESTIVE_SURCHARGE = 0.50;

    public double getPrices(RoomType roomType){
        return switch (roomType){
            case SINGLE -> 10_000.0;
            case DOUBLE -> 20_000.0;
            case SUITE -> 30_000.0;
        };
    }

    public double calculateTotal(RoomType roomType, int numberOfNights, boolean isFestiveSeason){
        double basePrice = getPrices(roomType);
        double subTotal = basePrice * numberOfNights;
        if(isFestiveSeason){
           return subTotal + (1 + FESTIVE_SURCHARGE);
        }else
            return subTotal;
    }

    public PaymentResponse calculatePaymentBreakdown (PaymentRequest paymentRequest){
        double basePrice = getPrices(paymentRequest.getRoomType());
        double subTotal = basePrice * paymentRequest.getNumberOfNights();
        double surchargeAmount = paymentRequest.isFestivePeriod() ? subTotal * FESTIVE_SURCHARGE : 0.0;
        double total = subTotal + surchargeAmount;

        return map(paymentRequest, basePrice, total, surchargeAmount);
    }

}
