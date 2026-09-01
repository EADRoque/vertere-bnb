package com.vertere.userservice.config;  //which folder/namespace this class belongs to

import org.springframework.context.annotation.Bean;   //marks a method whose return value Spring should manage as a bean
import org.springframework.context.annotation.Configuration;   //marks this class as a source of bean definitions
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;   //the actual hashing algorithm we use for passwords
import org.springframework.security.crypto.password.PasswordEncoder;   //the general-purpose interface other classes depend on

/**
 * This class wires up shared security-related beans for the app.
 * Right now that's just one thing: how we hash and verify passwords.
 *
 * - passwordEncoder: exposes a PasswordEncoder bean backed by BCrypt, a
 *   slow, salted hashing algorithm designed specifically for passwords
 *   (as opposed to a fast general-purpose hash like MD5/SHA, which would
 *   be unsafe here). Any other class - like UserService - that needs to
 *   hash or check a password gets this same bean injected automatically.
 */
@Configuration   //tells Spring "look in here for @Bean methods to register"
public class SecurityConfig {

    @Bean   //registers this as a shared, reusable bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();   //BCrypt: purpose-built, salted hashing for passwords
    }
}
