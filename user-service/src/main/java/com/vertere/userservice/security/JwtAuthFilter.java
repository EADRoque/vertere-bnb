package com.vertere.userservice.security;  //which folder/namespace this class belongs to

import java.io.IOException;   //thrown by servlet I/O, part of the filter method's signature
import java.util.List;   //holds the authorities (roles) we attach to the authenticated user

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;   //Spring Security's holder for "who is this request from"
import org.springframework.security.core.authority.SimpleGrantedAuthority;   //a single role/permission, e.g. ROLE_ADMIN
import org.springframework.security.core.context.SecurityContextHolder;   //where Spring Security keeps the current request's authentication
import org.springframework.stereotype.Component;   //tells Spring "create and manage one instance of this class"
import org.springframework.web.filter.OncePerRequestFilter;   //base class guaranteeing this filter runs exactly once per request

import io.jsonwebtoken.Claims;   //the decoded data stored inside a valid JWT
import jakarta.servlet.FilterChain;   //lets us pass the request along to the next filter/handler
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * This filter runs on every incoming HTTP request and checks for a JWT in
 * the Authorization header. If a valid token is found, it tells Spring
 * Security "this request is from this authenticated user" so downstream
 * code (like @PreAuthorize checks) knows who's making the call.
 *
 * - jwtService: used to verify the token's signature and pull out its claims.
 * - doFilterInternal: the actual per-request logic.
 *   1. Reads the "Authorization" header and checks it's a bearer token.
 *   2. Verifies the token and pulls the user id + admin flag out of it.
 *   3. Builds a Spring Security authentication object with the right role
 *      (ROLE_ADMIN or ROLE_USER) and stores it for the rest of the request.
 *   4. If anything goes wrong (missing/expired/tampered token), it's
 *      silently ignored here - the request just continues unauthenticated,
 *      and it's up to Spring Security's route rules whether that's allowed.
 *   5. Either way, the request is always passed on to the next filter.
 */
@Component   //Spring creates and manages a single instance of this filter
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {   //Spring automatically supplies this bean
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    )   throws ServletException, IOException {

        String header = request.getHeader("Authorization");   //expected format: "Bearer <token>"

        if (header != null && header.startsWith("Bearer ")) {   //only handle bearer tokens, ignore anything else
            String token = header.substring(7);   //strips the "Bearer " prefix, leaving just the token

            try {
                Claims claims = jwtService.verifyToken(token);   //throws if the token is invalid/expired/tampered with
                String userId = claims.getSubject();   //the user id we stored as the token's subject
                boolean isAdmin = claims.get("isAdmin", Boolean.class);   //the custom claim we stored at login

                var authorities = isAdmin   //map our simple boolean flag to a Spring Security role
                    ? List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                    : List.of(new SimpleGrantedAuthority("ROLE_USER"));

                var authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);   //no credentials needed, the token already proved identity
                SecurityContextHolder.getContext().setAuthentication(authentication);   //marks this request as authenticated for the rest of the pipeline

            } catch (Exception e) {
                // invalid/expired token — leave unauthenticated, let route rules decide
            }
        }

        filterChain.doFilter(request, response);   //always continue to the next filter/handler, authenticated or not
    }

}
