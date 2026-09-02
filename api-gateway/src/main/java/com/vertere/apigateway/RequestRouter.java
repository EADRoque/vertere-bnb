package com.vertere.apigateway;  //which folder/namespace this class belongs to

import java.util.LinkedHashMap;   //a Map that remembers insertion order - required here since route order determines matching priority
import java.util.Map;   //pairs a path pattern with the backend service that handles it

import org.springframework.stereotype.Component;   //tells Spring "manage this as a bean"

/**
 * This class decides which backend microservice a given request path
 * should be forwarded to. GatewayController calls into here for every
 * incoming request.
 *
 * - ROUTES: an ordered list of (path pattern -> backend URL) pairs.
 *   Order matters - the first pattern that matches wins, so more
 *   specific routes are listed before more general ones (e.g. the
 *   listing-reviews route before the plain /listings route, since both
 *   would otherwise match a reviews path).
 * - resolveTargetBaseUrl: checks the path against each pattern in order
 *   and returns the first matching backend's base URL, or throws if
 *   nothing matches.
 */
@Component   //makes this class a Spring-managed bean so it can be injected elsewhere (e.g. into GatewayController)
public class RequestRouter {

    private static final Map<String, String> ROUTES = new LinkedHashMap<>();
    static {
        // More specific routes MUST come before more general ones.
        ROUTES.put("^/listings/[^/]+/reviews", "http://localhost:8085"); // review-service - must be checked before the plain /listings route below
        ROUTES.put("^/auth", "http://localhost:8081");   //user-service
        ROUTES.put("^/users", "http://localhost:8081");   //user-service
        ROUTES.put("^/listings", "http://localhost:8082");   //listing-service
        ROUTES.put("^/bookings", "http://localhost:8083");   //booking-service
        ROUTES.put("^/payments", "http://localhost:8084");   //payment-service
        ROUTES.put("^/reviews", "http://localhost:8085");   //review-service
        ROUTES.put("^/conversations", "http://localhost:8086");   //messaging-service
        ROUTES.put("^/notifications", "http://localhost:8087");   //notification-service
    }

    public String resolveTargetBaseUrl(String downstreamPath) {
        for (Map.Entry<String, String> entry : ROUTES.entrySet()) {   //LinkedHashMap guarantees this walks the routes in the order they were added above
            if (downstreamPath.matches(entry.getKey() + ".*")) {   //match the pattern as a prefix, allowing anything after it
                return entry.getValue();
            }
        }
        throw new IllegalArgumentException("No route found for path: " + downstreamPath);   //no backend is configured to handle this path
    }

}