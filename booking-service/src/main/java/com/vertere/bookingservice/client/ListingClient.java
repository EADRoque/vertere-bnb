package com.vertere.bookingservice.client;  //which folder/namespace this class belongs to

import java.math.BigDecimal;   //precise number type used for money (basePrice)
import java.time.LocalDate;   //represents a calendar date with no time component
import java.util.List;   //the collection type used for blocked dates
import java.util.UUID;   //the type used for the listing's id

import org.springframework.stereotype.Component;   //tells Spring "manage this as a bean"
import org.springframework.web.client.RestClient;   //Spring's HTTP client used to call other services

/**
 * This class is booking-service's way of talking to listing-service over
 * HTTP, since each microservice owns its own data - booking-service
 * doesn't have direct database access to listings.
 *
 * - restClient: a pre-configured HTTP client pointed at listing-service,
 *   with a 3-second timeout on both connecting and reading so a slow or
 *   unreachable listing-service can't hang a booking request forever.
 *   Note: the base URL is hardcoded here rather than pulled from
 *   config/service discovery.
 * - getBasePrice: fetches a listing's nightly price by id.
 * - getBlockedDates: fetches which dates in a range are already blocked
 *   for a listing.
 * - ListingDto, AvailabilityDto: small private shapes that mirror just
 *   the fields we care about from listing-service's responses - we
 *   ignore everything else it sends back.
 */
@Component   //makes this class a Spring-managed bean so it can be injected elsewhere (e.g. into BookingService)
public class ListingClient {

    private final RestClient restClient;

    public ListingClient() {
        var requestFactory = new org.springframework.http.client.SimpleClientHttpRequestFactory();   //lets us configure timeouts on the underlying HTTP connection
        requestFactory.setConnectTimeout(3000);   //give up after 3s if listing-service can't even be reached
        requestFactory.setReadTimeout(3000);   //give up after 3s if listing-service is reachable but not responding

        this.restClient = RestClient.builder()
            .baseUrl("http://localhost:8082")   //where listing-service is expected to be running
            .build();
    }

    public BigDecimal getBasePrice(UUID listingId) {
        ListingDto listing = restClient.get()
            .uri("/listings/{id}", listingId)   //calls listing-service's GET /listings/{id} endpoint
            .retrieve()
            .body(ListingDto.class);   //deserializes just the fields ListingDto declares

        return listing.basePrice();
    }

    public List<LocalDate> getBlockedDates(UUID listingId, LocalDate start, LocalDate end) {
        AvailabilityDto availability = restClient.get()
            .uri("/listings/{id}/availability?start={start}&end={end}", listingId, start, end)   //calls listing-service's availability endpoint
            .retrieve()
            .body(AvailabilityDto.class);

        return availability.blockedDates();
    }

    private record ListingDto(BigDecimal basePrice) {}   //only the field we need from listing-service's full listing response
    private record AvailabilityDto(List<LocalDate> blockedDates) {}   //only the field we need from listing-service's availability response
}
