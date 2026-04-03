package com.hotel.management.service;

import com.hotel.management.dto.HotelRequest;
import com.hotel.management.dto.HotelResponse;
import com.hotel.management.exception.ResourceNotFoundException;
import com.hotel.management.model.Hotel;
import com.hotel.management.repository.HotelRepository;
import com.hotel.management.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelService {

    private final HotelRepository hotelRepository;
    private final ReviewRepository reviewRepository;

    public List<HotelResponse> getAllHotels() {
        return hotelRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<HotelResponse> searchHotelsByLocation(String location) {
        return hotelRepository.findByLocationContainingIgnoreCase(location).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public HotelResponse getHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));
        return toResponse(hotel);
    }

    @Transactional
    public HotelResponse createHotel(HotelRequest request) {
        Hotel hotel = Hotel.builder()
                .name(request.getName())
                .location(request.getLocation())
                .description(request.getDescription())
                .build();
        hotel = hotelRepository.save(hotel);
        log.info("Created hotel: {}", hotel.getName());
        return toResponse(hotel);
    }

    @Transactional
    public HotelResponse updateHotel(Long id, HotelRequest request) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));
        hotel.setName(request.getName());
        hotel.setLocation(request.getLocation());
        hotel.setDescription(request.getDescription());
        hotel = hotelRepository.save(hotel);
        log.info("Updated hotel: {}", hotel.getId());
        return toResponse(hotel);
    }

    @Transactional
    public void deleteHotel(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));
        hotelRepository.delete(hotel);
        log.info("Deleted hotel: {}", id);
    }

    private HotelResponse toResponse(Hotel hotel) {
        HotelResponse resp = HotelResponse.fromEntity(hotel);
        Double avg = reviewRepository.findAverageRatingByHotelId(hotel.getId());
        resp.setAverageRating(avg);
        return resp;
    }
}
