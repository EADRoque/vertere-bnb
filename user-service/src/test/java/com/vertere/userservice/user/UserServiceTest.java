package com.vertere.userservice.user;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vertere.userservice.security.JwtService;
import com.vertere.userservice.user.dto.LoginRequest;
import com.vertere.userservice.user.dto.RegisterRequest;
import com.vertere.userservice.user.dto.UpdateProfileRequest;
import com.vertere.userservice.user.dto.UserResponse;
import com.vertere.userservice.user.exception.EmailAlreadyExistsException;
import com.vertere.userservice.user.exception.InvalidCredentialsException;
import com.vertere.userservice.user.exception.UserNotFoundException;

/**
 * This class checks that UserService's business logic actually behaves the
 * way it's supposed to - without touching a real database, password
 * hasher, or token generator.
 *
 * - userRepository, passwordEncoder, jwtService: fake ("mock") stand-ins
 *   for the real dependencies, so we can control exactly what they return
 *   in each test instead of relying on a real database or real hashing.
 * - userService: the actual class under test, rebuilt fresh before every
 *   test so nothing leaks between tests.
 * - register_savesNewUser_whenEmailNotTaken: signing up with a free email
 *   should hash the password, save the user, and return their public info.
 * - register_throwsException_whenEmailAlreadyTaken: signing up with an
 *   email that's already in use should fail and never save anything.
 * - login_returnsToken_whenCredentialsAreCorrect: logging in with the
 *   right password should hand back a signed token.
 * - login_throwsException_whenPasswordIsWrong: logging in with the wrong
 *   password should fail with a vague error and never issue a token.
 */
@ExtendWith(MockitoExtension.class)   //tells the testing framework to set up the @Mock fields automatically
class UserServiceTest {

    @Mock
    private UserRepository userRepository;   //fake stand-in for the database

    @Mock
    private PasswordEncoder passwordEncoder;   //fake stand-in for password hashing/checking

    @Mock
    private JwtService jwtService;   //fake stand-in for generating login tokens

    private UserService userService;   //the real class we're testing, built with the fakes above

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder, jwtService);   //fresh instance before every test so tests don't affect each other
    }

    @Test
    void register_savesNewUser_whenEmailNotTaken() {
        RegisterRequest request = new RegisterRequest("new@example.com", "password123", "New User");   //pretend sign-up form

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);   //tell the fake database "this email is free"
        when(passwordEncoder.encode("password123")).thenReturn("hashed-password");   //tell the fake hasher what to return
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));   //tell the fake database to just hand back whatever it was given


        UserResponse response = userService.register(request);   //actually run the real registration logic

        assertThat(response.email()).isEqualTo("new@example.com");   //check the email came back correctly
        assertThat(response.fullName()).isEqualTo("New User");   //check the name came back correctly
        assertThat(response.admin()).isFalse();   //new users should never be admins by default

        verify(userRepository).save(any(User.class));   //confirm the new user was actually saved
    }

    @Test
    void register_throwsException_whenEmailAlreadyTaken() {
        RegisterRequest request = new RegisterRequest("taken@example.com", "password123", "Someone");   //pretend sign-up form with a used email

        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);   //tell the fake database "this email is already taken"

        assertThatThrownBy(() -> userService.register(request))   //expect registration to fail
            .isInstanceOf(EmailAlreadyExistsException.class)
            .hasMessage("Email already registered");

        verify(userRepository, never()).save(any(User.class));   //confirm it never tried to save the duplicate user
    }

    @Test
    void login_returnsToken_whenCredentialsAreCorrect() {
        User existingUser = new User("guest@example.com", "hashed-password", "Guest User");   //a user that's already "in the database"
        LoginRequest request = new LoginRequest("guest@example.com", "password123");   //pretend login attempt

        when(userRepository.findByEmail("guest@example.com")).thenReturn(Optional.of(existingUser));   //tell the fake database "this user exists"
        when(passwordEncoder.matches("password123", "hashed-password")).thenReturn(true);   //tell the fake hasher "yes, this password is correct"
        when(jwtService.generateToken(existingUser.getId(), existingUser.isAdmin())).thenReturn("fake-jwt-token");   //tell the fake token service what to hand back

        String token = userService.login(request);   //actually run the real login logic

        assertThat(token).isEqualTo("fake-jwt-token");   //confirm we got the expected token back, proving login succeeded

    }

    @Test
    void login_throwsException_whenPasswordIsWrong() {
        User existingUser = new User("guest@example.com", "hashed-password", "Guest User");   //a user that's already "in the database"
        LoginRequest request = new LoginRequest("guest@example.com", "wrongpassword");   //pretend login attempt with a bad password

        when(userRepository.findByEmail("guest@example.com")).thenReturn(Optional.of(existingUser));   //tell the fake database "this user exists"
        when(passwordEncoder.matches("wrongpassword", "hashed-password")).thenReturn(false);   //tell the fake hasher "no, this password does NOT match"

        assertThatThrownBy(() -> userService.login(request))   //expect login to fail
            .isInstanceOf(InvalidCredentialsException.class)
            .hasMessage("Invalid email or password");

        verify(jwtService, never()).generateToken(any(), anyBoolean());   //confirm no token was issued since the password was wrong

    }

    @Test
    void updateProfile_updatesFields_whenUserExists() {
        User existingUser = new User("guest@example.com", "hashed-password", "Old Name");
        UpdateProfileRequest request = new UpdateProfileRequest("New Name", "09171234567", "http://avatar.url/pic.png");

        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.updateProfile(UUID.randomUUID(), request);

        assertThat(response.fullName()).isEqualTo("New Name");
        assertThat(response.phone()).isEqualTo("09171234567");
        assertThat(response.avatarUrl()).isEqualTo("http://avatar.url/pic.png");
    }

    @Test
    void updateProfile_throwsException_whenUserNotFound() {
        UpdateProfileRequest request = new UpdateProfileRequest("New Name", "09171234567", "http://avatar.url/pic.png");

        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateProfile(UUID.randomUUID(), request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found");

        verify(userRepository, never()).save(any(User.class));
    }
}
    