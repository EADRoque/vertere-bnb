package com.vertere.apigateway;  //which folder/namespace this class belongs to

import java.util.LinkedHashMap;   //a Map that remembers insertion order - required here since route order determines matching priority
import java.util.Map;   //pairs a path pattern with the backend service that handles it

import org.springframework.beans.factory.annotation.Value;   //injects a value from application.properties/env
import org.springframework.stereotype.Component;   //tells Spring "manage this as a bean"

/**
 * This class decides which backend microservice a given request path
 * should be forwarded to. GatewayController calls into here for every
 * incoming request.
 *
 * - Each downstream service's base URL is injected from config (see
 *   application.properties), defaulting to its local dev port - so
 *   deployments can point at real container/host addresses without
 *   touching code, while `./gradlew bootRun` locally still works
 *   unchanged.
 * - ROUTES: an ordered list of (path pattern -> backend URL) pairs,
 *   built once in the constructor from those injected URLs. Order
 *   matters - the first pattern that matches wins, so more specific
 *   routes are listed before more general ones (e.g. the
 *   listing-reviews route before the plain /listings route, since both
 *   would otherwise match a reviews path).
 * - resolveTargetBaseUrl: checks the path against each pattern in order
 *   and returns the first matching backend's base URL, or throws if
 *   nothing matches.
 */
@Component   //makes this class a Spring-managed bean so it can be injected elsewhere (e.g. into GatewayController)
public class RequestRouter {

    private final Map<String, String> routes = new LinkedHashMap<>();

    public RequestRouter(   //Spring supplies each URL from config, falling back to the local dev port if unset
            @Value("${services.user-url:http://localhost:8081}") String userServiceUrl,
            @Value("${services.listing-url:http://localhost:8082}") String listingServiceUrl,
            @Value("${services.booking-url:http://localhost:8083}") String bookingServiceUrl,
            @Value("${services.payment-url:http://localhost:8084}") String paymentServiceUrl,
            @Value("${services.review-url:http://localhost:8085}") String reviewServiceUrl,
            @Value("${services.messaging-url:http://localhost:8086}") String messagingServiceUrl,
            @Value("${services.notification-url:http://localhost:8087}") String notificationServiceUrl
    ) {
        // More specific routes MUST come before more general ones.
        routes.put("^/listings/[^/]+/reviews", reviewServiceUrl); // review-service - must be checked before the plain /listings route below
        routes.put("^/auth", userServiceUrl);
        routes.put("^/users", userServiceUrl);
        routes.put("^/listings", listingServiceUrl);
        routes.put("^/bookings", bookingServiceUrl);
        routes.put("^/payments", paymentServiceUrl);
        routes.put("^/reviews", reviewServiceUrl);
        routes.put("^/conversations", messagingServiceUrl);
        routes.put("^/notifications", notificationServiceUrl);
    }

    public String resolveTargetBaseUrl(String downstreamPath) {
        for (Map.Entry<String, String> entry : routes.entrySet()) {   //LinkedHashMap guarantees this walks the routes in the order they were added above
            if (downstreamPath.matches(entry.getKey() + ".*")) {   //match the pattern as a prefix, allowing anything after it
                return entry.getValue();
            }
        }
        throw new IllegalArgumentException("No route found for path: " + downstreamPath);   //no backend is configured to handle this path
    }

}