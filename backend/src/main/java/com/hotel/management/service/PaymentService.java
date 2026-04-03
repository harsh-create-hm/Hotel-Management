package com.hotel.management.service;

import com.hotel.management.dto.PaymentResponse;
import com.hotel.management.exception.BadRequestException;
import com.hotel.management.exception.ResourceNotFoundException;
import com.hotel.management.model.Booking;
import com.hotel.management.model.Payment;
import com.hotel.management.repository.BookingRepository;
import com.hotel.management.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public PaymentResponse processPayment(Long bookingId, String userEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));

        if (!booking.getUser().getEmail().equals(userEmail)) {
            throw new BadRequestException("Not authorized to pay for this booking");
        }
        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new BadRequestException("Cannot pay for a cancelled booking");
        }
        if (booking.getStatus() == Booking.BookingStatus.CONFIRMED) {
            throw new BadRequestException("Booking is already confirmed");
        }

        long nights = ChronoUnit.DAYS.between(booking.getCheckIn(), booking.getCheckOut());
        BigDecimal amount = booking.getRoom().getPricePerNight().multiply(BigDecimal.valueOf(nights));

        // Simulate payment - always succeeds
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElse(Payment.builder().booking(booking).build());
        payment.setAmount(amount);
        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        payment = paymentRepository.save(payment);

        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        log.info("Payment processed for booking: {}, amount: {}", bookingId, amount);
        return PaymentResponse.fromEntity(payment);
    }

    public PaymentResponse getPaymentByBooking(Long bookingId) {
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for booking: " + bookingId));
        return PaymentResponse.fromEntity(payment);
    }
}
