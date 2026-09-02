package com.vertere.apigateway;  //which folder/namespace this class belongs to

import java.util.Enumeration;   //the old-style iterator type HttpServletRequest uses for header names

import org.springframework.http.HttpMethod;   //represents GET/POST/PUT/etc. so we can forward the same verb
import org.springframework.http.ResponseEntity;   //wraps a response body together with its status code/headers
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;   //Spring's HTTP client, used here to call the actual backend service

import jakarta.servlet.http.HttpServletRequest;   //gives raw access to the incoming request (method, headers, body, query string)

/**
 * This class is the single entry point every client request passes
 * through. It doesn't handle any business logic itself - it figures out
 * (via RequestRouter) which backend microservice owns the requested
 * path, forwards the request there almost as-is, and relays the
 * response back to the client.
 *
 * - restClient: the HTTP client used to actually call the backend
 *   service.
 * - requestRouter: decides which backend base URL a given path should
 *   go to.
 * - proxy: handles every request under /api/** - reconstructs the
 *   target URL, copies over the method, query string, headers (except
 *   host/content-length, which must be recalculated for the new
 *   destination), and body, sends it to the resolved backend, and
 *   returns that backend's response byte-for-byte and header-for-header.
 */
@RestController   //marks this as a REST controller whose method return values become the response body
public class GatewayController {

    private final RestClient restClient = RestClient.create();   //a plain client with no fixed base URL, since the target changes per request
    private final RequestRouter requestRouter;

    public GatewayController(RequestRouter requestRouter) {   //Spring automatically supplies this bean
        this.requestRouter = requestRouter;
    }

    @RequestMapping("/api/**")   //catches every request under /api, regardless of HTTP method
    public ResponseEntity<byte[]> proxy(HttpServletRequest request) throws Exception {

        String path = request.getRequestURI();
        String downstreamPath = path.substring(4);   //strip the leading "/api" prefix to get the path the backend actually expects
        String targetBaseUrl = requestRouter.resolveTargetBaseUrl(downstreamPath);   //e.g. "http://localhost:8082" for a /listings path

        String fullUrl = targetBaseUrl + downstreamPath;
        if (request.getQueryString() != null) {
            fullUrl += "?" + request.getQueryString();   //preserve query params like ?start=...&end=...
        }

        byte[] body = request.getInputStream().readAllBytes();   //read the raw request body so it can be forwarded unchanged

        var requestSpec = restClient
                .method(HttpMethod.valueOf(request.getMethod()))   //forward using the same HTTP method the client used (GET, POST, etc.)
                .uri(fullUrl);

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            if (!name.equalsIgnoreCase("host") && !name.equalsIgnoreCase("content-length")) {   //these two must be recomputed for the new destination, not copied verbatim
                requestSpec.header(name, request.getHeader(name));   //forwards things like Authorization so auth still works downstream
            }
        }

        if (body.length > 0) {
            requestSpec.body(body);
        }

        return requestSpec
                .exchange((clientRequest, clientResponse) ->   //exchange() gives full control over building the response, instead of retrieve() auto-throwing on error statuses
                        ResponseEntity
                                .status(clientResponse.getStatusCode())   //pass the backend's exact status code back to the client
                                .headers(clientResponse.getHeaders())   //pass the backend's exact headers back to the client
                                .body(clientResponse.getBody().readAllBytes())   //pass the backend's exact body back to the client
                );
    }

}