package com.vertere.reviewservice.review;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.vertere.reviewservice.review.dto.CreateReviewRequest;
import com.vertere.reviewservice.review.dto.HostResponseRequest;
import com.vertere.reviewservice.review.dto.ReviewResponse;

import jakarta.validation.Valid;

@RestController
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/reviews")
    public ResponseEntity<ReviewResponse> create(
            Authentication authentication,
            @Valid @RequestBody CreateReviewRequest request
    ) {
        UUID guestUserId = UUID.fromString(authentication.getName());
        ReviewResponse response = reviewService.createReview(guestUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/reviews/{id}/response")
    public ResponseEntity<ReviewResponse> respond(
            @PathVariable UUID id,
            @Valid @RequestBody HostResponseRequest request
    ) {
        ReviewResponse response = reviewService.addHostResponse(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/listings/{listingId}/reviews")
    public ResponseEntity<List<ReviewResponse>> getForListing(@PathVariable UUID listingId) {
        return ResponseEntity.ok(reviewService.getReviewsForListing(listingId));
    }

}