package com.vertere.apigateway;  //which folder/namespace this class belongs to

import org.slf4j.Logger;   //logs the real error server-side for anything unexpected
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;   //the status code we translate the exception into
import org.springframework.http.ResponseEntity;   //wraps a response body together with its status code
import org.springframework.web.bind.annotation.ExceptionHandler;   //marks a method as handling a specific exception type
import org.springframework.web.bind.annotation.RestControllerAdvice;   //applies these handlers globally, across every controller
import org.springframework.web.servlet.resource.NoResourceFoundException;   //thrown for any path with no matching route (e.g. Render's own "/" health check) - not a real gateway failure

/**
 * This class catches exceptions thrown anywhere in the gateway's request
 * handling and turns them into proper HTTP error responses, instead of
 * a raw 500 error leaking internal details. Unlike the backend services,
 * the gateway had no exception handler at all before this - any failure
 * (an unknown route, a downstream service being unreachable) would
 * surface Spring's default error page.
 *
 * - handleUnknownRoute: RequestRouter throws IllegalArgumentException
 *   when a path matches no configured backend - turned into a 404
 *   instead of a 500, since it's really "not found", not a server bug.
 * - handleUnexpected: catches everything else (a downstream service
 *   being unreachable, an unexpected proxying failure) - logs the real
 *   error server-side but returns a generic 502 so internal details
 *   never leak to the client.
 */
@RestControllerAdvice   //applies to every controller in the app, no need to repeat this per-controller
public class GatewayExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GatewayExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)   //thrown by RequestRouter.resolveTargetBaseUrl for an unrecognized path
    public ResponseEntity<String> handleUnknownRoute(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)   //a path with no matching route at all (e.g. "/" - Render's own health check target) - a plain 404, not a gateway failure
    public ResponseEntity<String> handleNoRoute(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
    }

    @ExceptionHandler(Exception.class)   //catch-all - only reached when nothing more specific above matched
    public ResponseEntity<String> handleUnexpected(Exception ex) {
        log.error("Unexpected gateway error", ex);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Something went wrong reaching the backend. Please try again.");
    }

}
