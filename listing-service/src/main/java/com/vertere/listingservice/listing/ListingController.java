package com.vertere.listingservice.listing;  //which folder/namespace this class belongs to

import com.vertere.listingservice.listing.dto.*;   //all the request/response shapes used by this controller
import jakarta.validation.Valid;   //triggers automatic validation of an incoming request body
import org.springframework.data.domain.Page;   //a page of search results
import org.springframework.data.domain.Pageable;   //describes which page/size the client wants, bound from query params
import org.springframework.http.HttpStatus;   //used to explicitly set response status codes
import org.springframework.http.ResponseEntity;   //wraps a response body together with its status code/headers
import org.springframework.security.core.Authentication;   //holds info about the currently authenticated caller, set by JwtAuthFilter
import org.springframework.web.bind.annotation.*;   //the HTTP mapping annotations (@GetMapping, @PostMapping, etc.)

import java.time.LocalDate;   //used for the availability date-range query params
import java.util.UUID;   //the type used for listing/user ids

/**
 * This is the HTTP entry point for everything related to listings - it
 * translates incoming requests into calls on ListingService and wraps
 * the results as HTTP responses. It doesn't contain business logic
 * itself.
 *
 * - listingService: does the actual work; this class just adapts HTTP
 *   in and out.
 * - create: POST a new listing, owned by whoever is authenticated.
 * - get: GET a single listing by id (public, no auth required).
 * - update: PUT changes to a listing (must be the owner).
 * - deactivate: DELETE (soft-delete) a listing (must be the owner).
 * - search: GET a filtered, paged list of active listings (public).
 * - blockDate: mark a date unavailable on a listing (must be the owner).
 * - setPriceOverride: set a custom price for a date (must be the owner).
 * - getAvailability: GET the blocked dates and price overrides for a
 *   listing within a date range (public).
 */
@RestController   //marks this as a REST controller whose method return values become the response body
@RequestMapping("/listings")   //every endpoint here is under /listings
public class ListingController {

    private final ListingService listingService;

    public ListingController(ListingService listingService) {   //Spring automatically supplies this bean
        this.listingService = listingService;
    }

    @PostMapping
    public ResponseEntity<ListingResponse> create(
            Authentication authentication,   //injected by Spring Security based on the verified JWT
            @Valid @RequestBody CreateListingRequest request   //validated against its annotations before this method runs
    ) {
        UUID hostUserId = UUID.fromString(authentication.getName());   //the "name" here is the user id we put in the token's subject
        ListingResponse response = listingService.createListing(hostUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);   //201 Created
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListingResponse> get(@PathVariable UUID id) {   //no Authentication param - this route is public (see SecurityFilterConfig)
        return ResponseEntity.ok(listingService.getListing(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ListingResponse> update(
            @PathVariable UUID id,
            Authentication authentication,
            @Valid @RequestBody UpdateListingRequest request
    ) {
        UUID requestingUserId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(listingService.updateListing(id, requestingUserId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        UUID requestingUserId = UUID.fromString(authentication.getName());
        listingService.deactivateListing(id, requestingUserId);
        return ResponseEntity.noContent().build();   //204 No Content - nothing meaningful to return
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ListingResponse>> search(ListingSearchRequest request, Pageable pageable) {   //filters and paging are bound straight from query params
        return ResponseEntity.ok(listingService.searchListings(request, pageable));
    }

    @PutMapping("/{id}/availability/block")
    public ResponseEntity<Void> blockDate(
            @PathVariable UUID id,
            Authentication authentication,
            @Valid @RequestBody BlockDateRequest request
    ) {
        UUID requestingUserId = UUID.fromString(authentication.getName());
        listingService.blockDate(id, requestingUserId, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/availability/price")
    public ResponseEntity<Void> setPriceOverride(
            @PathVariable UUID id,
            Authentication authentication,
            @Valid @RequestBody PriceOverrideRequest request
    ) {
        UUID requestingUserId = UUID.fromString(authentication.getName());
        listingService.setPriceOverride(id, requestingUserId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<AvailabilityResponse> getAvailability(
            @PathVariable UUID id,
            @RequestParam LocalDate start,   //e.g. ?start=2026-09-01
            @RequestParam LocalDate end   //e.g. &end=2026-09-30
    ) {
        return ResponseEntity.ok(listingService.getAvailability(id, start, end));
    }

}