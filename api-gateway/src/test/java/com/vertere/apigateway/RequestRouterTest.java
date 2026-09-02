package com.vertere.apigateway;  //which folder/namespace this class belongs to

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

/**
 * This class checks that RequestRouter sends each kind of path to the
 * right backend service - no database or Spring context needed, since
 * RequestRouter is just plain logic over a hardcoded map.
 *
 * - router: a real RequestRouter instance (not a mock) - there's nothing
 *   to fake here, it's pure logic.
 * - resolvesToUserService_forAuthPaths: both /auth and /users paths
 *   should route to user-service.
 * - resolvesToListingService_forPlainListingPaths: a plain /listings
 *   path should route to listing-service.
 * - resolvesToReviewService_forListingReviewsPath_evenThoughItStartsWithListings:
 *   this is the important edge case - a /listings/{id}/reviews path
 *   must route to review-service, not listing-service, proving the more
 *   specific route correctly wins even though the path also starts with
 *   "/listings".
 * - resolvesToBookingService_forBookingPaths: a /bookings path should
 *   route to booking-service.
 * - throwsException_forUnknownPath: a path matching no known route
 *   should fail loudly instead of silently going nowhere.
 */
class RequestRouterTest {

    private final RequestRouter router = new RequestRouter();   //no mocking needed - this class has no external dependencies

    @Test
    void resolvesToUserService_forAuthPaths() {
        assertEquals("http://localhost:8081", router.resolveTargetBaseUrl("/auth/login"));
        assertEquals("http://localhost:8081", router.resolveTargetBaseUrl("/users/me"));
    }

    @Test
    void resolvesToListingService_forPlainListingPaths() {
        assertEquals("http://localhost:8082", router.resolveTargetBaseUrl("/listings/search"));
    }

    @Test
    void resolvesToReviewService_forListingReviewsPath_evenThoughItStartsWithListings() {
        String path = "/listings/26ab7066-32f3-412e-abb1-9a1d435327f8/reviews";   //looks like a /listings path, but should NOT match the /listings route
        assertEquals("http://localhost:8085", router.resolveTargetBaseUrl(path));   //proves the more specific reviews route was checked first
    }

    @Test
    void resolvesToBookingService_forBookingPaths() {
        assertEquals("http://localhost:8083", router.resolveTargetBaseUrl("/bookings/mine"));
    }

    @Test
    void throwsException_forUnknownPath() {
        assertThrows(IllegalArgumentException.class, () -> router.resolveTargetBaseUrl("/nonexistent/path"));   //no route matches, so this should fail rather than silently pick something
    }

}