package com.hotel.management.dto;

import com.hotel.management.model.Hotel;
import lombok.Data;

@Data
public class HotelResponse {
    private Long id;
    private String name;
    private String location;
    private String description;
    private Double averageRating;

    public static HotelResponse fromEntity(Hotel hotel) {
        HotelResponse resp = new HotelResponse();
        resp.setId(hotel.getId());
        resp.setName(hotel.getName());
        resp.setLocation(hotel.getLocation());
        resp.setDescription(hotel.getDescription());
        return resp;
    }
}
