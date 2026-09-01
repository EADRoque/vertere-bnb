package com.vertere.userservice.config;  //which folder/namespace this class belongs to

import org.springframework.context.annotation.Bean;   //marks a method whose return value Spring should manage as a bean
import org.springframework.context.annotation.Configuration;   //marks this class as a source of bean definitions
import org.springframework.http.HttpStatus;   //lets us configure how Spring Security handles HTTP requests
import org.springframework.security.config.annotation.web.builders.HttpSecurity;   //controls whether Spring creates/uses server-side sessions
import org.springframework.security.config.http.SessionCreationPolicy;   //the finished set of security rules Spring Security applies to every request
import org.springframework.security.web.SecurityFilterChain;   //Spring's built-in filter, used here just as a position marker
import org.springframework.security.web.authentication.HttpStatusEntryPoint;   //our custom filter that reads/verifies the JWT on each request
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.vertere.userservice.security.JwtAuthFilter;
/**
 * This class defines the overall security rules for the whole app - which
 * routes anyone can hit, which routes require a valid token, and where
 * our custom JwtAuthFilter fits into Spring Security's filter pipeline.
 *
 * - jwtAuthFilter: our filter that checks for a JWT and marks the request
 *   as authenticated if one is present and valid.
 * - securityFilterChain: builds the actual rule set.
 *   - csrf disabled: CSRF protection is for browser/cookie-based sessions;
 *     we don't use those here (auth is via bearer tokens), so it's not needed.
 *   - sessionCreationPolicy STATELESS: tells Spring Security not to create
 *     or rely on server-side sessions - every request must prove who it
 *     is via its own token, since we don't remember anything between requests.
 *   - requestMatchers(...).permitAll(): register and login must be
 *     reachable without already having a token (you need to log in to
 *     get one in the first place).
 *   - anyRequest().authenticated(): everything else requires a valid,
 *     authenticated request.
 *   - addFilterBefore: slots our jwtAuthFilter in before Spring's default
 *     username/password filter, so our token check runs first on every
 *     request.
 */
@Configuration   //tells Spring "look in here for @Bean methods to register"
public class SecurityFilterConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityFilterConfig(JwtAuthFilter jwtAuthFilter) {   //Spring automatically supplies this bean
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean   //registers the resulting filter chain as the app's security configuration
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(ex ->
            ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/register", "/auth/login").permitAll()
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);   //run our JWT check before Spring's default auth filter

        return http.build();
    }

}
