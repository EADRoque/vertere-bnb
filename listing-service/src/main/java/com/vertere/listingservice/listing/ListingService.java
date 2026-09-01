package com.vertere.listingservice.listing;  //which folder/namespace this class belongs to

import java.math.BigDecimal;   //precise number type used for money (price, fees)
import java.time.LocalDate;   //represents a calendar date with no time component
import java.util.List;   //the collection type used for amenity names and blocked dates
import java.util.Map;   //pairs dates with their custom prices
import java.util.UUID;   //the type used for ids throughout this service
import java.util.stream.Collectors;   //used to turn streams into lists/maps

import org.springframework.data.domain.Page;   //a page of search results (for pagination)
import org.springframework.data.domain.Pageable;   //describes which page/size the client wants
import org.springframework.data.jpa.domain.Specification;   //combinable database filter, built from ListingSpecifications
import org.springframework.stereotype.Service;   //tells Spring "this class holds business logic, manage it as a bean"

import com.vertere.listingservice.listing.dto.AvailabilityResponse;   //the shape of an availability lookup result
import com.vertere.listingservice.listing.dto.BlockDateRequest;   //the shape of an incoming "block this date" request
import com.vertere.listingservice.listing.dto.CreateListingRequest;   //the shape of an incoming "create a listing" request
import com.vertere.listingservice.listing.dto.ListingResponse;   //the shape of what we send back about a listing
import com.vertere.listingservice.listing.dto.ListingSearchRequest;   //the shape of an incoming search request
import com.vertere.listingservice.listing.dto.PriceOverrideRequest;   //the shape of an incoming "set custom price" request
import com.vertere.listingservice.listing.dto.UpdateListingRequest;   //the shape of an incoming "update this listing" request
import com.vertere.listingservice.listing.exception.ListingNotFoundException;   //thrown when a listing id doesn't exist
import com.vertere.listingservice.listing.exception.NotListingOwnerException;   //thrown when a user tries to modify a listing they don't own

/**
 * This class holds the actual business logic for working with listings -
 * the controller layer calls into here instead of talking to the
 * database directly.
 *
 * - listingRepository, amenityRepository, blockedDateRepository,
 *   priceOverrideRepository: how this service reads/writes the
 *   corresponding rows in the database.
 * - createListing: builds a new Listing, attaches any requested
 *   amenities (reusing existing ones or creating new ones by name),
 *   saves it, and returns it as a ListingResponse.
 * - getListing: looks up a listing by id or throws if it doesn't exist.
 * - updateListing: makes sure the requester owns the listing, then
 *   overwrites its editable fields and saves it.
 * - deactivateListing: makes sure the requester owns the listing, then
 *   marks it inactive instead of deleting it.
 * - blockDate: makes sure the requester owns the listing, then marks a
 *   date unavailable (skips if it's already blocked).
 * - setPriceOverride: makes sure the requester owns the listing, then
 *   saves a custom price for a specific date.
 * - getAvailability: looks up the blocked dates and price overrides for
 *   a listing within a date range.
 * - searchListings: builds a dynamic filter from the search request and
 *   returns a page of matching, active listings.
 * - toResponse: a small private helper that converts our internal
 *   Listing entity (plus its amenities) into the safe, public-facing
 *   ListingResponse shape.
 */
@Service   //makes this class a Spring-managed bean so it can be injected elsewhere (e.g. into a controller)
public class ListingService {

    private final ListingRepository listingRepository;
    private final AmenityRepository amenityRepository;
    private final BlockedDateRepository blockedDateRepository;
    private final PriceOverrideRepository priceOverrideRepository;

    public ListingService(   //Spring automatically supplies these beans
        ListingRepository listingRepository,
        AmenityRepository amenityRepository,
        BlockedDateRepository blockedDateRepository,
        PriceOverrideRepository priceOverrideRepository
    ) {
        this.listingRepository = listingRepository;
        this.amenityRepository = amenityRepository;
        this.blockedDateRepository = blockedDateRepository;
        this.priceOverrideRepository = priceOverrideRepository;
    }

    public ListingResponse createListing(UUID hostUserId, CreateListingRequest request) {
        Listing listing = new Listing(   //build the entity with the required fields
                hostUserId,
                request.title(),
                request.description(),
                request.propertyType(),
                request.city(),
                request.country(),
                request.maxGuests(),
                request.basePrice(),
                request.cleaningFee(),
                request.cancellationPolicy()
        );

        if (request.amenityNames() != null) {   //amenities are optional
            for (String name : request.amenityNames()) {
                Amenity amenity = amenityRepository.findByName(name)   //reuse the amenity if it already exists
                        .orElseGet(() -> amenityRepository.save(new Amenity(name)));   //otherwise create it on the fly
                listing.addAmenity(amenity);
            }
        }

        Listing saved = listingRepository.save(listing);   //INSERT the new row and get back the saved entity (now with its generated id)
        return toResponse(saved);   //convert to the public-facing shape before returning

    }

    public ListingResponse getListing(UUID id) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found"));   //stop early if no such listing exists
        return toResponse(listing);
    }

    public ListingResponse updateListing(UUID id, UUID requestingUserId, UpdateListingRequest request) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found"));   //stop early if no such listing exists

        if (!listing.getHostUserId().equals(requestingUserId)) {   //only the host who owns this listing can edit it
            throw new NotListingOwnerException("You do not own this listing");
        }

        listing.setTitle(request.title());
        listing.setDescription(request.description());
        listing.setPropertyType(request.propertyType());
        listing.setCity(request.city());
        listing.setCountry(request.country());
        listing.setMaxGuests(request.maxGuests());
        listing.setBasePrice(request.basePrice());
        listing.setCleaningFee(request.cleaningFee());
        listing.setCancellationPolicy(request.cancellationPolicy());

        Listing saved = listingRepository.save(listing);   //persist the changes
        return toResponse(saved);
    }

    public void deactivateListing(UUID id, UUID requestingUserId) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found"));   //stop early if no such listing exists

        if (!listing.getHostUserId().equals(requestingUserId)) {   //only the host who owns this listing can deactivate it
            throw new NotListingOwnerException("You do not own this listing");
        }

        listing.setActive(false);   //soft-delete: the listing stays in the database but is hidden/unbookable
        listingRepository.save(listing);
    }

    public void blockDate(UUID listingId, UUID requestingUserId, BlockDateRequest request) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found"));   //stop early if no such listing exists

        if (!listing.getHostUserId().equals(requestingUserId)) {   //only the host who owns this listing can block dates on it
            throw new NotListingOwnerException("You do not own this listing");
        }

        if (!blockedDateRepository.existsByListingIdAndBlockedDate(listingId, request.date())) {   //avoid saving a duplicate blocked date
            blockedDateRepository.save(new BlockedDate(listingId, request.date()));
        }
    }

    public void setPriceOverride(UUID listingId, UUID requestingUserId, PriceOverrideRequest request) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found"));   //stop early if no such listing exists

        if (!listing.getHostUserId().equals(requestingUserId)) {   //only the host who owns this listing can set custom prices on it
            throw new NotListingOwnerException("You do not own this listing");
        }

        priceOverrideRepository.save(new PriceOverride(listingId, request.date(), request.price()));   //note: this doesn't replace an existing override for the same date, it just adds a new row
    }

    public AvailabilityResponse getAvailability(UUID listingId, LocalDate start, LocalDate end) {
        List<LocalDate> blocked = blockedDateRepository
                .findByListingIdAndBlockedDateBetween(listingId, start, end)
                .stream()
                .map(BlockedDate::getBlockedDate)   //we only need the date itself, not the whole entity
                .collect(Collectors.toList());

        Map<LocalDate, BigDecimal> overrides = priceOverrideRepository
                .findByListingIdAndOverrideDateBetween(listingId, start, end)
                .stream()
                .collect(Collectors.toMap(PriceOverride::getOverrideDate, PriceOverride::getPrice));   //turn the list into a date -> price lookup

        return new AvailabilityResponse(blocked, overrides);
    }

    public Page<ListingResponse> searchListings(ListingSearchRequest request, Pageable pageable) {
        Specification<Listing> spec = Specification
                .where(ListingSpecifications.isActive())   //always exclude deactivated listings
                .and(ListingSpecifications.hasCity(request.city()))   //each of these is a no-op filter if the field wasn't provided
                .and(ListingSpecifications.hasMinGuests(request.minGuests()))
                .and(ListingSpecifications.hasMinPrice(request.minPrice()))
                .and(ListingSpecifications.hasMaxPrice(request.maxPrice()));

        return listingRepository.findAll(spec, pageable)   //run the combined filter, one page at a time
                .map(this::toResponse);   //convert each result to the public-facing shape
    }

    private ListingResponse toResponse(Listing listing) {   //maps a Listing entity to a ListingResponse, flattening amenities down to their names
        List<String> amenityNames = listing.getAmenities().stream()
                .map(Amenity::getName)
                .collect(Collectors.toList());

        return new ListingResponse(
                listing.getId(),
                listing.getHostUserId(),
                listing.getTitle(),
                listing.getDescription(),
                listing.getPropertyType(),
                listing.getCity(),
                listing.getCountry(),
                listing.getMaxGuests(),
                listing.getBasePrice(),
                listing.getCleaningFee(),
                listing.getCancellationPolicy(),
                listing.isActive(),
                listing.getCreatedAt(),
                amenityNames
        );
    }

}