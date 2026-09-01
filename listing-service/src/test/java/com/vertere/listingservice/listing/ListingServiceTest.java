package com.vertere.listingservice.listing;  //which folder/namespace this class belongs to

import com.vertere.listingservice.listing.dto.CreateListingRequest;   //the shape of an incoming "create a listing" request
import com.vertere.listingservice.listing.dto.ListingResponse;   //the shape of what we get back about a listing
import com.vertere.listingservice.listing.dto.UpdateListingRequest;   //the shape of an incoming "update this listing" request
import com.vertere.listingservice.listing.exception.ListingNotFoundException;   //expected when a listing id doesn't exist
import com.vertere.listingservice.listing.exception.NotListingOwnerException;   //expected when a non-owner tries to modify a listing
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * This class checks that ListingService's business logic actually
 * behaves the way it's supposed to - without touching a real database.
 *
 * - listingRepository, amenityRepository, blockedDateRepository,
 *   priceOverrideRepository: fake ("mock") stand-ins for the real
 *   dependencies, so we can control exactly what they return in each
 *   test instead of relying on a real database.
 * - listingService: the actual class under test; @InjectMocks builds it
 *   automatically and wires in the mocks above.
 * - hostId: a fresh random user id generated before every test, used to
 *   represent "the listing's owner".
 * - createListing_savesListing_andReturnsResponse: creating a listing
 *   with an amenity should save it and return the expected details.
 * - getListing_throwsException_whenListingDoesNotExist: looking up a
 *   listing id that doesn't exist should fail.
 * - updateListing_updatesFields_whenRequesterIsOwner: the owner should be
 *   able to update their own listing's fields.
 * - updateListing_throwsException_whenRequesterIsNotOwner: a non-owner
 *   should be blocked from updating someone else's listing, and nothing
 *   should be saved.
 * - deactivateListing_setsActiveFalse_whenRequesterIsOwner: the owner
 *   deactivating their listing should flip it to inactive and save it.
 */
@ExtendWith(MockitoExtension.class)   //tells the testing framework to set up the @Mock/@InjectMocks fields automatically
class ListingServiceTest {

    @Mock
    private ListingRepository listingRepository;   //fake stand-in for the listings database

    @Mock
    private AmenityRepository amenityRepository;   //fake stand-in for the amenities database

    @Mock
    private BlockedDateRepository blockedDateRepository;   //fake stand-in for the blocked-dates database

    @Mock
    private PriceOverrideRepository priceOverrideRepository;   //fake stand-in for the price-overrides database

    @InjectMocks
    private ListingService listingService;   //the real class we're testing, auto-built with the mocks above injected in

    private UUID hostId;

    @BeforeEach
    void setUp() {
        hostId = UUID.randomUUID();   //fresh random "owner" id before every test so tests don't affect each other
    }

    @Test
    void createListing_savesListing_andReturnsResponse() {
        CreateListingRequest request = new CreateListingRequest(
                "Test Title", "Test Description", "Apartment",
                "Manila", "Philippines", 2,
                new BigDecimal("1000.00"), new BigDecimal("100.00"),
                "FLEXIBLE", List.of("WiFi")
        );

        when(amenityRepository.findByName("WiFi")).thenReturn(Optional.of(new Amenity("WiFi")));   //tell the fake database "this amenity already exists"
        when(listingRepository.save(any(Listing.class))).thenAnswer(invocation -> invocation.getArgument(0));   //tell the fake database to just hand back whatever it was given

        ListingResponse response = listingService.createListing(hostId, request);   //actually run the real creation logic

        assertEquals("Test Title", response.title());   //check the title came back correctly
        assertEquals(hostId, response.hostUserId());   //check the host id came back correctly
        assertTrue(response.active());   //new listings should default to active

        verify(listingRepository).save(any(Listing.class));   //confirm the new listing was actually saved
    }

    @Test
    void getListing_throwsException_whenListingDoesNotExist() {
        UUID randomId = UUID.randomUUID();
        when(listingRepository.findById(randomId)).thenReturn(Optional.empty());   //tell the fake database "no listing with this id"

        assertThrows(ListingNotFoundException.class, () -> listingService.getListing(randomId));   //expect the lookup to fail
    }

    @Test
    void updateListing_updatesFields_whenRequesterIsOwner() {
        Listing existingListing = new Listing(   //an existing listing owned by hostId, as if it came from the database
                hostId, "Old Title", "Old Description", "Apartment",
                "Manila", "Philippines", 2,
                new BigDecimal("1000.00"), new BigDecimal("100.00"), "FLEXIBLE"
        );
        UUID listingId = UUID.randomUUID();

        UpdateListingRequest request = new UpdateListingRequest(   //the new values being requested
                "New Title", "New Description", "Apartment",
                "Cebu", "Philippines", 3,
                new BigDecimal("1500.00"), new BigDecimal("150.00"), "STRICT"
        );

        when(listingRepository.findById(listingId)).thenReturn(Optional.of(existingListing));   //tell the fake database "this listing exists"
        when(listingRepository.save(any(Listing.class))).thenAnswer(invocation -> invocation.getArgument(0));   //tell the fake database to just hand back whatever it was given

        ListingResponse response = listingService.updateListing(listingId, hostId, request);   //hostId is the owner, so this should succeed

        assertEquals("New Title", response.title());   //check the title was updated
        assertEquals("Cebu", response.city());   //check the city was updated
    }

    @Test
    void updateListing_throwsException_whenRequesterIsNotOwner() {
        Listing existingListing = new Listing(   //an existing listing owned by hostId
                hostId, "Old Title", "Old Description", "Apartment",
                "Manila", "Philippines", 2,
                new BigDecimal("1000.00"), new BigDecimal("100.00"), "FLEXIBLE"
        );
        UUID listingId = UUID.randomUUID();
        UUID someoneElseId = UUID.randomUUID();   //a different user, not the owner

        UpdateListingRequest request = new UpdateListingRequest(
                "New Title", "New Description", "Apartment",
                "Cebu", "Philippines", 3,
                new BigDecimal("1500.00"), new BigDecimal("150.00"), "STRICT"
        );

        when(listingRepository.findById(listingId)).thenReturn(Optional.of(existingListing));   //tell the fake database "this listing exists"

        assertThrows(NotListingOwnerException.class,   //expect the update to be blocked since someoneElseId isn't the owner
                () -> listingService.updateListing(listingId, someoneElseId, request));

        verify(listingRepository, never()).save(any(Listing.class));   //confirm nothing was saved since the update was rejected
    }

    @Test
    void deactivateListing_setsActiveFalse_whenRequesterIsOwner() {
        Listing existingListing = new Listing(   //an existing, active listing owned by hostId
                hostId, "Title", "Description", "Apartment",
                "Manila", "Philippines", 2,
                new BigDecimal("1000.00"), new BigDecimal("100.00"), "FLEXIBLE"
        );
        UUID listingId = UUID.randomUUID();

        when(listingRepository.findById(listingId)).thenReturn(Optional.of(existingListing));   //tell the fake database "this listing exists"
        when(listingRepository.save(any(Listing.class))).thenAnswer(invocation -> invocation.getArgument(0));

        listingService.deactivateListing(listingId, hostId);   //hostId is the owner, so this should succeed

        assertFalse(existingListing.isActive());   //confirm the listing object itself was flipped to inactive
        verify(listingRepository).save(existingListing);   //confirm the change was saved
    }

}