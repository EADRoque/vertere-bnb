package com.vertere.listingservice.listing;  //which folder/namespace this class belongs to

import java.math.BigDecimal;   //precise number type used for money (price)

import org.springframework.data.jpa.domain.Specification;   //represents one reusable, combinable database filter

/**
 * This class holds small, reusable search filters for listings. Each
 * method builds one filter; ListingService combines the ones it needs
 * with .and() to build a dynamic search query (see searchListings).
 *
 * - hasCity: filters to listings in a given city, or applies no filter
 *   if city is null.
 * - hasMinGuests: filters to listings that fit at least this many
 *   guests, or applies no filter if null.
 * - hasMinPrice / hasMaxPrice: filters to listings within a price range,
 *   or applies no filter if null.
 * - isActive: filters to only listings that are currently active
 *   (visible/bookable).
 */
public class ListingSpecifications {

    public static Specification<Listing> hasCity(String city) {
        return (root, query, cb) -> city == null ? null : cb.equal(root.get("city"), city);   //no filter if city wasn't provided
    }

    public static Specification<Listing> hasMinGuests(Integer minGuests) {
        return (root, query, cb) -> minGuests == null ? null : cb.greaterThanOrEqualTo(root.get("maxGuests"), minGuests);   //no filter if minGuests wasn't provided
    }

    public static Specification<Listing> hasMinPrice(BigDecimal minPrice) {
        return (root, query, cb) -> minPrice == null ? null : cb.greaterThanOrEqualTo(root.get("basePrice"), minPrice);   //no filter if minPrice wasn't provided
    }

    public static Specification<Listing> hasMaxPrice(BigDecimal maxPrice) {
        return (root, query, cb) -> maxPrice == null ? null : cb.lessThanOrEqualTo(root.get("basePrice"), maxPrice);   //no filter if maxPrice wasn't provided
    }

    public static Specification<Listing> isActive() {
        return (root, query, cb) -> cb.isTrue(root.get("active"));   //always applied - inactive listings never show up in search
    }

}