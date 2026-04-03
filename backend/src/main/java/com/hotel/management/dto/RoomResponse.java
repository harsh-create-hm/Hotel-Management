package com.hotel.management.dto;

import com.hotel.management.model.Room;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomResponse {
    private Long id;
    private Long hotelId;
    private String hotelName;
    private Room.RoomType type;
    private BigDecimal pricePerNight;
    private int availableCount;

    public static RoomResponse fromEntity(Room room) {
        RoomResponse resp = new RoomResponse();
        resp.setId(room.getId());
        resp.setHotelId(room.getHotel().getId());
        resp.setHotelName(room.getHotel().getName());
        resp.setType(room.getType());
        resp.setPricePerNight(room.getPricePerNight());
        resp.setAvailableCount(room.getAvailableCount());
        return resp;
    }
}
