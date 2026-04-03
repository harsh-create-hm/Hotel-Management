package com.hotel.management.controller;

import com.hotel.management.dto.PaymentResponse;
import com.hotel.management.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/booking/{bookingId}")
    public ResponseEntity<PaymentResponse> processPayment(@PathVariable Long bookingId,
                                                          @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(paymentService.processPayment(bookingId, userDetails.getUsername()));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long bookingId) {
        return ResponseEntity.ok(paymentService.getPaymentByBooking(bookingId));
    }
}
