package com.hotel.management.service;

import com.hotel.management.dto.BookingRequest;
import com.hotel.management.dto.BookingResponse;
import com.hotel.management.exception.BadRequestException;
import com.hotel.management.exception.ResourceNotFoundException;
import com.hotel.management.model.Booking;
import com.hotel.management.model.Room;
import com.hotel.management.model.User;
import com.hotel.management.repository.BookingRepository;
import com.hotel.management.repository.RoomRepository;
import com.hotel.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Transactional
    public BookingResponse createBooking(BookingRequest request, String userEmail) {
        if (!request.getCheckOut().isAfter(request.getCheckIn())) {
            throw new BadRequestException("Check-out date must be after check-in date");
        }
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        // Optimistic locking via @Version on Room.version field protects all Room updates,
        // including changes to availableCount, preventing concurrent overbooking.
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + request.getRoomId()));
        if (room.getAvailableCount() <= 0) {
            throw new BadRequestException("No available rooms of this type");
        }
        room.setAvailableCount(room.getAvailableCount() - 1);
        roomRepository.save(room);

        Booking booking = Booking.builder()
                .user(user)
                .room(room)
                .checkIn(request.getCheckIn())
                .checkOut(request.getCheckOut())
                .status(Booking.BookingStatus.CREATED)
                .build();
        booking = bookingRepository.save(booking);
        log.info("Booking created: {} for user: {}", booking.getId(), userEmail);

        BookingResponse resp = BookingResponse.fromEntity(booking);
        long nights = ChronoUnit.DAYS.between(request.getCheckIn(), request.getCheckOut());
        resp.setTotalAmount(room.getPricePerNight().multiply(BigDecimal.valueOf(nights)));
        return resp;
    }

    public List<BookingResponse> getUserBookings(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return bookingRepository.findByUserId(user.getId()).stream()
                .map(b -> {
                    BookingResponse resp = BookingResponse.fromEntity(b);
                    long nights = ChronoUnit.DAYS.between(b.getCheckIn(), b.getCheckOut());
                    resp.setTotalAmount(b.getRoom().getPricePerNight().multiply(BigDecimal.valueOf(nights)));
                    return resp;
                })
                .collect(Collectors.toList());
    }

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(b -> {
                    BookingResponse resp = BookingResponse.fromEntity(b);
                    long nights = ChronoUnit.DAYS.between(b.getCheckIn(), b.getCheckOut());
                    resp.setTotalAmount(b.getRoom().getPricePerNight().multiply(BigDecimal.valueOf(nights)));
                    return resp;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingResponse cancelBooking(Long bookingId, String userEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!booking.getUser().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new BadRequestException("Not authorized to cancel this booking");
        }
        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new BadRequestException("Booking is already cancelled");
        }
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        // Restore room availability
        Room room = booking.getRoom();
        room.setAvailableCount(room.getAvailableCount() + 1);
        roomRepository.save(room);
        booking = bookingRepository.save(booking);
        log.info("Booking cancelled: {}", bookingId);

        BookingResponse resp = BookingResponse.fromEntity(booking);
        long nights = ChronoUnit.DAYS.between(booking.getCheckIn(), booking.getCheckOut());
        resp.setTotalAmount(room.getPricePerNight().multiply(BigDecimal.valueOf(nights)));
        return resp;
    }
}
