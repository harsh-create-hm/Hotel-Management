package com.hotel.management.dto;

import com.hotel.management.model.Payment;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentResponse {
    private Long id;
    private Long bookingId;
    private Payment.PaymentStatus status;
    private BigDecimal amount;

    public static PaymentResponse fromEntity(Payment payment) {
        PaymentResponse resp = new PaymentResponse();
        resp.setId(payment.getId());
        resp.setBookingId(payment.getBooking().getId());
        resp.setStatus(payment.getStatus());
        resp.setAmount(payment.getAmount());
        return resp;
    }
}
