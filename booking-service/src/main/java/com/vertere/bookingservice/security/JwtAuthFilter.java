package com.vertere.bookingservice.security;  //which folder/namespace this class belongs to

import java.io.IOException;   //required by the servlet filter method signature
import java.util.List;   //holds the one authority (role) granted to the request

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;   //represents "this request is authenticated as this user"
import org.springframework.security.core.authority.SimpleGrantedAuthority;   //a single role, like ROLE_USER
import org.springframework.security.core.context.SecurityContextHolder;   //where Spring Security stores the current request's authentication
import org.springframework.stereotype.Component;   //tells Spring "manage this as a bean"
import org.springframework.web.filter.OncePerRequestFilter;   //base class that guarantees this filter runs exactly once per request

import io.jsonwebtoken.Claims;   //the decoded contents of a verified token
import jakarta.servlet.FilterChain;   //lets us hand the request off to the next filter/handler
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * This filter runs on every incoming request and checks for a JWT in the
 * Authorization header. If a valid token is found, it marks the request
 * as authenticated so downstream code (like SecurityFilterConfig's rules)
 * knows who's making the request.
 *
 * - jwtService: used to verify and decode the token.
 * - doFilterInternal: reads the "Bearer <token>" header, verifies it, and
 *   if valid, sets the authenticated user (and their role) for this
 *   request. If the header is missing or the token is invalid, the
 *   request just continues unauthenticated - it's up to the route rules
 *   to decide if that's allowed.
 */
@Component   //makes this class a Spring-managed bean so it can be wired into the security filter chain
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {   //only handle standard bearer-token headers
            String token = header.substring(7);   //strip off the "Bearer " prefix to get just the token

            try {
                Claims claims = jwtService.verifyToken(token);   //throws if the token is invalid/expired
                String userId = claims.getSubject();
                boolean isAdmin = claims.get("isAdmin", Boolean.class);

                var authorities = isAdmin
                        ? List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                        : List.of(new SimpleGrantedAuthority("ROLE_USER"));

                var authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);   //mark this request as authenticated for the rest of the pipeline

            } catch (Exception e) {
                
            }
        }

        filterChain.doFilter(request, response);   //always continue to the next filter/handler, authenticated or not
    }

}