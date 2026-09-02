package com.vertere.bookingservice.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ListingClient {

    private final RestClient restClient;

    public ListingClient() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8082")
                .build();
    }

    public BigDecimal getBasePrice(UUID listingId, String authHeader) {
        ListingDto listing = restClient.get()
                .uri("/listings/{id}", listingId)
                .header("Authorization", authHeader)
                .retrieve()
                .body(ListingDto.class);

        return listing.basePrice();
    }

    public List<LocalDate> getBlockedDates(UUID listingId, LocalDate start, LocalDate end, String authHeader) {
        AvailabilityDto availability = restClient.get()
                .uri("/listings/{id}/availability?start={start}&end={end}", listingId, start, end)
                .header("Authorization", authHeader)
                .retrieve()
                .body(AvailabilityDto.class);

        return availability.blockedDates();
    }

    private record ListingDto(BigDecimal basePrice) {}
    private record AvailabilityDto(List<LocalDate> blockedDates) {}

}