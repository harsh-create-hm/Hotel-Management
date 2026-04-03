package com.hotel.management.dto;

import com.hotel.management.model.Room;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomRequest {
    @NotNull
    private Room.RoomType type;

    @NotNull
    private BigDecimal pricePerNight;

    @Min(0)
    private int availableCount;
}
