package com.vertere.userservice.user;  //which folder/namespace this class belongs to

import org.springframework.http.HttpStatus;   //standard HTTP status codes, e.g. 201 Created
import org.springframework.http.ResponseEntity;   //wraps a response body together with its status code/headers
import org.springframework.web.bind.annotation.PostMapping;   //maps a method to handle POST requests on a given path
import org.springframework.web.bind.annotation.RequestBody;   //tells Spring to deserialize the request's JSON body into this parameter
import org.springframework.web.bind.annotation.RequestMapping;   //sets the base path shared by every endpoint in this controller
import org.springframework.web.bind.annotation.RestController;   //marks this class as a REST endpoint, responses are serialized straight to JSON

import com.vertere.userservice.user.dto.LoginRequest;   //the shape of an incoming login request
import com.vertere.userservice.user.dto.RegisterRequest;   //the shape of an incoming sign-up request
import com.vertere.userservice.user.dto.UserResponse;   //the safe, public-facing shape of a user

import jakarta.validation.Valid;   //tells Spring to run the @NotBlank/@Email/@Size checks on the request body before the method runs

/**
 * This is the entry point for authentication over HTTP - it's the thing
 * that actually receives requests from the outside world and hands them
 * off to UserService, which does the real work.
 *
 * - userService: where the actual registration/login logic lives; this
 *   controller stays thin and just deals with HTTP concerns.
 * - register: handles POST /auth/register. Validates the incoming body,
 *   creates the new user, and responds with 201 Created plus the new
 *   user's public info.
 * - login: handles POST /auth/login. Validates the incoming body, checks
 *   the credentials, and responds with 200 OK plus a signed JWT the
 *   client should send on future requests.
 */
@RestController   //this class's methods return response bodies directly (as JSON), not view names
@RequestMapping("/auth")   //every endpoint below is under /auth/...
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {   //Spring automatically supplies this bean
        this.userService = userService;
    }

    @PostMapping("/register")   //handles POST /auth/register
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {   //@Valid triggers the DTO's validation annotations
        UserResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);   //201 Created - a new resource (the user) was made
    }

    @PostMapping("/login")   //handles POST /auth/login
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {
        String token = userService.login(request);   //throws if the credentials are invalid, handled elsewhere as an error response
        return ResponseEntity.ok(token);   //200 OK with the JWT as the plain response body
    }
}
