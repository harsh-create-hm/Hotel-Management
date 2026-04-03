package com.hotel.management.dto;

import com.hotel.management.model.Booking;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BookingResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long roomId;
    private String roomType;
    private Long hotelId;
    private String hotelName;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Booking.BookingStatus status;
    private BigDecimal totalAmount;

    public static BookingResponse fromEntity(Booking booking) {
        BookingResponse resp = new BookingResponse();
        resp.setId(booking.getId());
        resp.setUserId(booking.getUser().getId());
        resp.setUserName(booking.getUser().getName());
        resp.setRoomId(booking.getRoom().getId());
        resp.setRoomType(booking.getRoom().getType().name());
        resp.setHotelId(booking.getRoom().getHotel().getId());
        resp.setHotelName(booking.getRoom().getHotel().getName());
        resp.setCheckIn(booking.getCheckIn());
        resp.setCheckOut(booking.getCheckOut());
        resp.setStatus(booking.getStatus());
        return resp;
    }
}
