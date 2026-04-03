package com.hotel.management.service;

import com.hotel.management.dto.HotelRequest;
import com.hotel.management.dto.HotelResponse;
import com.hotel.management.exception.ResourceNotFoundException;
import com.hotel.management.model.Hotel;
import com.hotel.management.repository.HotelRepository;
import com.hotel.management.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private HotelService hotelService;

    private Hotel hotel;

    @BeforeEach
    void setUp() {
        hotel = Hotel.builder()
                .id(1L)
                .name("Grand Hotel")
                .location("New York")
                .description("Luxury hotel")
                .build();
    }

    @Test
    void getAllHotels_returnsHotelList() {
        when(hotelRepository.findAll()).thenReturn(List.of(hotel));
        when(reviewRepository.findAverageRatingByHotelId(1L)).thenReturn(4.5);

        List<HotelResponse> result = hotelService.getAllHotels();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Grand Hotel");
        assertThat(result.get(0).getAverageRating()).isEqualTo(4.5);
    }

    @Test
    void getHotelById_found_returnsHotel() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(reviewRepository.findAverageRatingByHotelId(1L)).thenReturn(null);

        HotelResponse result = hotelService.getHotelById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getLocation()).isEqualTo("New York");
    }

    @Test
    void getHotelById_notFound_throwsException() {
        when(hotelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hotelService.getHotelById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createHotel_savesAndReturnsHotel() {
        HotelRequest request = new HotelRequest();
        request.setName("New Hotel");
        request.setLocation("London");
        request.setDescription("Nice place");

        Hotel saved = Hotel.builder().id(2L).name("New Hotel").location("London").description("Nice place").build();
        when(hotelRepository.save(any(Hotel.class))).thenReturn(saved);
        when(reviewRepository.findAverageRatingByHotelId(2L)).thenReturn(null);

        HotelResponse result = hotelService.createHotel(request);

        assertThat(result.getName()).isEqualTo("New Hotel");
        assertThat(result.getId()).isEqualTo(2L);
        verify(hotelRepository, times(1)).save(any(Hotel.class));
    }

    @Test
    void updateHotel_notFound_throwsException() {
        when(hotelRepository.findById(99L)).thenReturn(Optional.empty());

        HotelRequest request = new HotelRequest();
        request.setName("X");
        request.setLocation("Y");

        assertThatThrownBy(() -> hotelService.updateHotel(99L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteHotel_notFound_throwsException() {
        when(hotelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hotelService.deleteHotel(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchHotelsByLocation_returnsFilteredList() {
        when(hotelRepository.findByLocationContainingIgnoreCase("New York")).thenReturn(List.of(hotel));
        when(reviewRepository.findAverageRatingByHotelId(1L)).thenReturn(null);

        List<HotelResponse> result = hotelService.searchHotelsByLocation("New York");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLocation()).isEqualTo("New York");
    }
}
