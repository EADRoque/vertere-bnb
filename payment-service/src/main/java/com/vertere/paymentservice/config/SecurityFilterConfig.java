package com.vertere.paymentservice.config;  //which folder/namespace this class belongs to

import com.vertere.paymentservice.security.JwtAuthFilter;   //our custom filter that reads and verifies JWTs
import org.springframework.context.annotation.Bean;   //marks a method whose return value Spring should manage
import org.springframework.context.annotation.Configuration;   //tells Spring "this class defines beans/config"
import org.springframework.http.HttpStatus;   //the status code returned when auth fails
import org.springframework.security.config.annotation.web.builders.HttpSecurity;   //the builder used to configure security rules
import org.springframework.security.config.http.SessionCreationPolicy;   //controls whether Spring creates HTTP sessions
import org.springframework.security.web.SecurityFilterChain;   //the finished, built set of security rules
import org.springframework.security.web.authentication.HttpStatusEntryPoint;   //what to send back when an unauthenticated user hits a protected route
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;   //the built-in filter our custom JWT filter runs before

/**
 * This class configures how Spring Security handles every incoming
 * request - how authentication is checked (via our JwtAuthFilter instead
 * of sessions or basic auth), and which routes require it.
 *
 * - jwtAuthFilter: the filter that reads the Authorization header and
 *   marks requests as authenticated.
 * - securityFilterChain: builds the actual rule set - disables CSRF (not
 *   needed for a stateless, token-based API), disables server-side
 *   sessions, returns 401 instead of a login redirect when auth is
 *   missing, requires authentication for every request (there are no
 *   public routes in payment-service), and plugs in jwtAuthFilter to do
 *   that authentication check.
 */
@Configuration   //tells Spring "this class defines beans/config to load at startup"
public class SecurityFilterConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityFilterConfig(JwtAuthFilter jwtAuthFilter) {   //Spring automatically supplies this bean
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean   //Spring will call this once at startup and manage the resulting SecurityFilterChain
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())   //not needed - this is a stateless API, not a browser session app
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))   //don't create/use HTTP sessions; every request must carry its own token
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))   //return 401 instead of redirecting to a login page
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()   //every route requires a valid token
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);   //run our JWT check before Spring's default auth filter

        return http.build();
    }

}