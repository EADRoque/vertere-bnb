package com.vertere.userservice.security;  //which folder/namespace this class belongs to

import java.util.Date;   //represents a point in time, used for issuedAt/expiry
import java.util.UUID;   //represents a universally unique ID, used for the user's id

import javax.crypto.SecretKey;   //the secret key used to sign and verify tokens

import org.springframework.beans.factory.annotation.Value;   //injects a value from application.properties/yml
import org.springframework.stereotype.Service;   //tells Spring "this class holds business logic, manage it as a bean"

import io.jsonwebtoken.Claims;   //the decoded body of a JWT (the data we stored inside it)
import io.jsonwebtoken.Jws;   //a JWT whose signature has already been checked
import io.jsonwebtoken.Jwts;   //entry point for building and parsing JWTs
import io.jsonwebtoken.security.Keys;   //helper for building a SecretKey from raw bytes

/**
 * This class is in charge of creating and checking JWTs (JSON Web Tokens) -
 * the signed strings we hand out after login so the user doesn't have to
 * send their password on every request.
 *
 * - key: the secret used to sign every token we create, and to verify
 *   every token we're handed back. Built once, from the "jwt.secret"
 *   value in application.properties/yml.
 * - expirationMs: how long (in milliseconds) a token stays valid for,
 *   also read from configuration ("jwt.expiration-ms").
 * - generateToken: builds a new signed token for a given user. The
 *   token's "subject" is the user's id, it carries an extra "isAdmin"
 *   claim, and it records when it was issued and when it expires.
 * - verifyToken: takes a token string, checks its signature against our
 *   key (throwing if it's invalid/expired/tampered with), and returns the
 *   claims (the data) stored inside it.
 */
@Service   //makes this class a Spring-managed bean so it can be injected elsewhere (e.g. into a filter or controller)
public class JwtService {

    private final SecretKey key;
    private final long expirationMs;

    public JwtService(
        @Value("${jwt.secret}") String secret,   //read from config, then turned into a proper signing key below
        @Value("${jwt.expiration-ms}") long expirationMs   //read from config, how long tokens last

    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());   //turns the raw secret string into a key usable for HMAC signing
        this.expirationMs = expirationMs;
    }

    public String generateToken(UUID userId, boolean isAdmin) {
        Date now = new Date();   //when this token is being created
        Date expiry = new Date(now.getTime() + expirationMs);   //when this token stops being valid

        return Jwts.builder()
            .subject(userId.toString())   //who this token is about
            .claim("isAdmin", isAdmin)   //extra piece of data we want to carry inside the token
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key)   //signs the token with our secret key so it can't be tampered with undetected
            .compact();   //serializes everything into the final JWT string
    }

    public Claims verifyToken(String token) {
        Jws<Claims> parsed = Jwts.parser()
            .verifyWith(key)   //checks the token's signature against our key - throws if it doesn't match
            .build()
            .parseSignedClaims(token);   //parses the token string and verifies it in one step

        return parsed.getPayload();   //the actual claims/data stored inside the token
    }
}
