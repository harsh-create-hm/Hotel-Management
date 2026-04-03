package com.hotel.management.service;

import com.hotel.management.dto.ReviewRequest;
import com.hotel.management.dto.ReviewResponse;
import com.hotel.management.exception.ResourceNotFoundException;
import com.hotel.management.model.Hotel;
import com.hotel.management.model.Review;
import com.hotel.management.model.User;
import com.hotel.management.repository.HotelRepository;
import com.hotel.management.repository.ReviewRepository;
import com.hotel.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewResponse createReview(ReviewRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found: " + request.getHotelId()));
        Review review = Review.builder()
                .user(user)
                .hotel(hotel)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();
        review = reviewRepository.save(review);
        log.info("Review created for hotel {} by user {}", request.getHotelId(), userEmail);
        return ReviewResponse.fromEntity(review);
    }

    public List<ReviewResponse> getHotelReviews(Long hotelId) {
        return reviewRepository.findByHotelId(hotelId).stream()
                .map(ReviewResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
