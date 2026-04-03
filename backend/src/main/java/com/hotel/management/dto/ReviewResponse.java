package com.hotel.management.dto;

import com.hotel.management.model.Review;
import lombok.Data;

@Data
public class ReviewResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long hotelId;
    private String hotelName;
    private int rating;
    private String comment;

    public static ReviewResponse fromEntity(Review review) {
        ReviewResponse resp = new ReviewResponse();
        resp.setId(review.getId());
        resp.setUserId(review.getUser().getId());
        resp.setUserName(review.getUser().getName());
        resp.setHotelId(review.getHotel().getId());
        resp.setHotelName(review.getHotel().getName());
        resp.setRating(review.getRating());
        resp.setComment(review.getComment());
        return resp;
    }
}
