package com.hotel.management.service;

import com.hotel.management.dto.RoomRequest;
import com.hotel.management.dto.RoomResponse;
import com.hotel.management.exception.ResourceNotFoundException;
import com.hotel.management.model.Hotel;
import com.hotel.management.model.Room;
import com.hotel.management.repository.HotelRepository;
import com.hotel.management.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    public List<RoomResponse> getRoomsByHotel(Long hotelId) {
        return roomRepository.findByHotelId(hotelId).stream()
                .map(RoomResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<RoomResponse> getAvailableRoomsByHotel(Long hotelId) {
        return roomRepository.findByHotelIdAndAvailableCountGreaterThan(hotelId, 0).stream()
                .map(RoomResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public RoomResponse getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
        return RoomResponse.fromEntity(room);
    }

    @Transactional
    public RoomResponse createRoom(Long hotelId, RoomRequest request) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + hotelId));
        Room room = Room.builder()
                .hotel(hotel)
                .type(request.getType())
                .pricePerNight(request.getPricePerNight())
                .availableCount(request.getAvailableCount())
                .build();
        room = roomRepository.save(room);
        log.info("Created room {} for hotel {}", room.getId(), hotelId);
        return RoomResponse.fromEntity(room);
    }

    @Transactional
    public RoomResponse updateRoom(Long id, RoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
        room.setType(request.getType());
        room.setPricePerNight(request.getPricePerNight());
        room.setAvailableCount(request.getAvailableCount());
        room = roomRepository.save(room);
        log.info("Updated room: {}", id);
        return RoomResponse.fromEntity(room);
    }

    @Transactional
    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
        roomRepository.delete(room);
        log.info("Deleted room: {}", id);
    }
}
