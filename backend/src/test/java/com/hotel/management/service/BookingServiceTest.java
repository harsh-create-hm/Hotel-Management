package com.hotel.management.service;

import com.hotel.management.dto.BookingRequest;
import com.hotel.management.dto.BookingResponse;
import com.hotel.management.exception.BadRequestException;
import com.hotel.management.exception.ResourceNotFoundException;
import com.hotel.management.model.Booking;
import com.hotel.management.model.Hotel;
import com.hotel.management.model.Room;
import com.hotel.management.model.User;
import com.hotel.management.repository.BookingRepository;
import com.hotel.management.repository.RoomRepository;
import com.hotel.management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    private User user;
    private Hotel hotel;
    private Room room;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).name("Alice").email("alice@test.com")
                .password("pass").role(User.Role.USER).build();

        hotel = Hotel.builder().id(1L).name("Grand Hotel").location("NY").build();

        room = Room.builder().id(1L).hotel(hotel)
                .type(Room.RoomType.DOUBLE)
                .pricePerNight(BigDecimal.valueOf(100))
                .availableCount(5)
                .build();

        booking = Booking.builder().id(1L).user(user).room(room)
                .checkIn(LocalDate.now().plusDays(1))
                .checkOut(LocalDate.now().plusDays(3))
                .status(Booking.BookingStatus.CREATED)
                .build();
    }

    @Test
    void createBooking_success() {
        BookingRequest request = new BookingRequest();
        request.setRoomId(1L);
        request.setCheckIn(LocalDate.now().plusDays(1));
        request.setCheckOut(LocalDate.now().plusDays(3));

        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(user));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.save(any())).thenReturn(room);
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingResponse result = bookingService.createBooking(request, "alice@test.com");

        assertThat(result).isNotNull();
        assertThat(result.getRoomId()).isEqualTo(1L);
        assertThat(result.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(200));
    }

    @Test
    void createBooking_invalidDates_throwsBadRequest() {
        BookingRequest request = new BookingRequest();
        request.setRoomId(1L);
        request.setCheckIn(LocalDate.now().plusDays(3));
        request.setCheckOut(LocalDate.now().plusDays(1));

        assertThatThrownBy(() -> bookingService.createBooking(request, "alice@test.com"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Check-out");
    }

    @Test
    void createBooking_noAvailableRooms_throwsBadRequest() {
        room.setAvailableCount(0);
        BookingRequest request = new BookingRequest();
        request.setRoomId(1L);
        request.setCheckIn(LocalDate.now().plusDays(1));
        request.setCheckOut(LocalDate.now().plusDays(3));

        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(user));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        assertThatThrownBy(() -> bookingService.createBooking(request, "alice@test.com"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("available");
    }

    @Test
    void cancelBooking_success() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(user));
        when(roomRepository.save(any())).thenReturn(room);
        booking.setStatus(Booking.BookingStatus.CREATED);
        Booking cancelled = Booking.builder().id(1L).user(user).room(room)
                .checkIn(booking.getCheckIn()).checkOut(booking.getCheckOut())
                .status(Booking.BookingStatus.CANCELLED).build();
        when(bookingRepository.save(any())).thenReturn(cancelled);

        BookingResponse result = bookingService.cancelBooking(1L, "alice@test.com");

        assertThat(result.getStatus()).isEqualTo(Booking.BookingStatus.CANCELLED);
    }

    @Test
    void cancelBooking_alreadyCancelled_throwsBadRequest() {
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> bookingService.cancelBooking(1L, "alice@test.com"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already cancelled");
    }

    @Test
    void getUserBookings_returnsUserBookings() {
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(user));
        when(bookingRepository.findByUserId(1L)).thenReturn(List.of(booking));

        List<BookingResponse> result = bookingService.getUserBookings("alice@test.com");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserName()).isEqualTo("Alice");
    }

    @Test
    void createBooking_roomNotFound_throwsException() {
        BookingRequest request = new BookingRequest();
        request.setRoomId(99L);
        request.setCheckIn(LocalDate.now().plusDays(1));
        request.setCheckOut(LocalDate.now().plusDays(3));

        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(user));
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createBooking(request, "alice@test.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
