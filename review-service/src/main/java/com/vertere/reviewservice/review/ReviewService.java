package com.vertere.reviewservice.review;

import com.vertere.reviewservice.review.dto.CreateReviewRequest;
import com.vertere.reviewservice.review.dto.HostResponseRequest;
import com.vertere.reviewservice.review.dto.ReviewResponse;
import com.vertere.reviewservice.review.exception.ReviewNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public ReviewResponse createReview(UUID guestUserId, CreateReviewRequest request) {
        Review review = new Review(
                request.listingId(),
                request.bookingId(),
                guestUserId,
                request.rating(),
                request.comment()
        );

        Review saved = reviewRepository.save(review);
        return toResponse(saved);
    }

    public ReviewResponse addHostResponse(UUID reviewId, HostResponseRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));

        review.setHostResponse(request.response());
        Review saved = reviewRepository.save(review);
        return toResponse(saved);
    }

    public List<ReviewResponse> getReviewsForListing(UUID listingId) {
        return reviewRepository.findByListingId(listingId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ReviewResponse toResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getListingId(),
                review.getBookingId(),
                review.getGuestUserId(),
                review.getRating(),
                review.getComment(),
                review.getHostResponse(),
                review.getCreatedAt()
        );
    }

}