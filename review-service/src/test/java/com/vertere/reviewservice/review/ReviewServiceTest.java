package com.vertere.reviewservice.review;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vertere.reviewservice.review.dto.CreateReviewRequest;
import com.vertere.reviewservice.review.dto.HostResponseRequest;
import com.vertere.reviewservice.review.dto.ReviewResponse;
import com.vertere.reviewservice.review.exception.ReviewNotFoundException;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    private UUID guestId;
    private UUID listingId;
    private UUID bookingId;

    @BeforeEach
    void setUp() {
        guestId = UUID.randomUUID();
        listingId = UUID.randomUUID();
        bookingId = UUID.randomUUID();
    }

    @Test
    void createReview_savesReview_andReturnsResponse() {
        CreateReviewRequest request = new CreateReviewRequest(listingId, bookingId, 5, "Great stay!");

        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReviewResponse response = reviewService.createReview(guestId, request);

        assertEquals(5, response.rating());
        assertEquals("Great stay!", response.comment());
        assertNull(response.hostResponse());
    }

    @Test
    void addHostResponse_setsResponse_whenReviewExists() {
        Review existingReview = new Review(listingId, bookingId, guestId, 4, "Nice place");
        UUID reviewId = UUID.randomUUID();
        HostResponseRequest request = new HostResponseRequest("Thanks for visiting!");

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existingReview));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReviewResponse response = reviewService.addHostResponse(reviewId, request);

        assertEquals("Thanks for visiting!", response.hostResponse());
    }

    @Test
    void addHostResponse_throwsException_whenReviewNotFound() {
        UUID reviewId = UUID.randomUUID();
        HostResponseRequest request = new HostResponseRequest("Thanks!");

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        assertThrows(ReviewNotFoundException.class, () -> reviewService.addHostResponse(reviewId, request));
    }

    @Test
    void getReviewsForListing_returnsAllReviews_forThatListing() {
        Review review1 = new Review(listingId, bookingId, guestId, 5, "Great!");
        Review review2 = new Review(listingId, UUID.randomUUID(), UUID.randomUUID(), 3, "It was okay");

        when(reviewRepository.findByListingId(listingId)).thenReturn(List.of(review1, review2));

        List<ReviewResponse> responses = reviewService.getReviewsForListing(listingId);

        assertEquals(2, responses.size());
    }

}