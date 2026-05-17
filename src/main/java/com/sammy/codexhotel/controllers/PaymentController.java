package com.sammy.codexhotel.controllers;

import com.sammy.codexhotel.dtos.requests.PaymentRequest;
import com.sammy.codexhotel.dtos.responses.ApiResponse;
import com.sammy.codexhotel.dtos.responses.PaymentResponse;
import com.sammy.codexhotel.services.PricingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PricingService pricingService;

    @PostMapping("/calculate")
    public ResponseEntity<ApiResponse> calculatePayment(@Valid @RequestBody PaymentRequest paymentRequest) {
        PaymentResponse paymentResponse = pricingService.calculatePaymentBreakdown(paymentRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse(true,"Payment successful",paymentResponse));
    }
}
