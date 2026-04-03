package com.hotel.management.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequest {
    @NotNull
    private Long roomId;

    @NotNull
    private LocalDate checkIn;

    @NotNull
    @Future
    private LocalDate checkOut;
}
