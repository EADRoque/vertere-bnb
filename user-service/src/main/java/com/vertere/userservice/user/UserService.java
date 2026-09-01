package com.vertere.userservice.user;  //which folder/namespace this class belongs to

import java.util.UUID;   //creates the signed token we hand back after a successful login

import org.springframework.security.crypto.password.PasswordEncoder;   //the shape of an incoming login request
import org.springframework.stereotype.Service;   //the shape of an incoming sign-up request

import com.vertere.userservice.security.JwtService;   //the shape of what we send back about a user
import com.vertere.userservice.user.dto.LoginRequest;   //hashes/checks passwords, wired up in SecurityConfig
import com.vertere.userservice.user.dto.RegisterRequest;   //tells Spring "this class holds business logic, manage it as a bean"
import com.vertere.userservice.user.dto.UpdateProfileRequest;
import com.vertere.userservice.user.dto.UserResponse;
import com.vertere.userservice.user.exception.EmailAlreadyExistsException;
import com.vertere.userservice.user.exception.InvalidCredentialsException;
import com.vertere.userservice.user.exception.UserNotFoundException;

/**
 * This class holds the actual business logic for working with users -
 * the controller layer calls into here instead of talking to the
 * database or hashing passwords itself.
 *
 * - userRepository: how this service reads/writes User rows in the database.
 * - passwordEncoder: how this service turns a plain password into a
 *   scrambled hash (and, elsewhere, checks a plain password against one).
 * - jwtService: how this service issues a signed token once a login
 *   succeeds, so the client doesn't have to send the password again.
 * - register: makes sure the email isn't already taken, hashes the
 *   password so it's never stored in plain text, saves the new User, and
 *   returns it as a UserResponse (so we never leak the password hash back
 *   to the client).
 * - toResponse: a small private helper that converts our internal User
 *   entity into the safe, public-facing UserResponse shape.
 * - login: looks up the user by email, checks the given password against
 *   the stored hash, and - if they match - returns a freshly generated
 *   JWT. Both failure cases (no such email, wrong password) throw the
 *   same generic error so we don't reveal which one it was.
 */
@Service   //makes this class a Spring-managed bean so it can be injected elsewhere (e.g. into a controller)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {   //Spring automatically supplies these beans
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {   //stop early if someone already signed up with this email
            throw new EmailAlreadyExistsException("Email already registered");
        }

        String hashedPassword = passwordEncoder.encode(request.password());   //scramble the plain password before it ever touches the database

        User user = new User(request.email(), hashedPassword, request.fullName());   //build the entity with the required fields
        User saved = userRepository.save(user);   //INSERT the new row and get back the saved entity (now with its generated id)

        return toResponse(saved);   //convert to the public-facing shape before returning
    }

    private UserResponse toResponse(User user) {   //maps a User entity to a UserResponse, leaving out sensitive fields like passwordHash
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getFullName(),
            user.getPhone(),
            user.getAvatarUrl(),
            user.isAdmin(),
            user.getCreatedAt()
        );

    }

    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));   //deliberately vague - don't reveal that the email doesn't exist

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {   //compares the plain password against the stored hash
            throw new InvalidCredentialsException("Invalid email or password");   //same generic message as above, for the same reason
        }

        return jwtService.generateToken(user.getId(), user.isAdmin());   //login succeeded - hand back a signed token instead of the User itself
    }

    public UserResponse getById(UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        return toResponse(user);
    }

    public UserResponse updateProfile(UUID id, UpdateProfileRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        user.setFullName(request.fullName());
        user.setPhone(request.phone());
        user.setAvatarUrl(request.avatarUrl());

        User saved = userRepository.save(user);
        return toResponse(saved);
    }
}
