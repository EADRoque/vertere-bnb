package com.vertere.listingservice.security;  //which folder/namespace this class belongs to

import java.util.Date;   //used for the token's issued-at and expiry timestamps
import java.util.UUID;   //the type used for the user id embedded in the token

import javax.crypto.SecretKey;   //the key used to sign and verify tokens

import org.springframework.beans.factory.annotation.Value;   //injects a value from application.properties
import org.springframework.stereotype.Service;   //tells Spring "this class holds business logic, manage it as a bean"

import io.jsonwebtoken.Claims;   //the decoded payload of a JWT (user id, isAdmin, etc.)
import io.jsonwebtoken.Jws;   //a JWT that's been verified and parsed
import io.jsonwebtoken.Jwts;   //builds and parses JWTs
import io.jsonwebtoken.security.Keys;   //builds a signing key from a raw secret

/**
 * This class handles creating and verifying JWTs (JSON Web Tokens) - the
 * signed tokens clients send to prove who they are, instead of resending
 * credentials on every request. Note: this service only verifies tokens
 * issued elsewhere (e.g. by user-service) since listing-service doesn't
 * handle login itself.
 *
 * - key: the secret key used to sign/verify tokens, loaded from config.
 * - expirationMs: how long a freshly generated token stays valid for.
 * - generateToken: builds a signed token containing the user's id and
 *   admin status.
 * - verifyToken: checks a token's signature and expiry, and returns its
 *   decoded contents.
 */
@Service   //makes this class a Spring-managed bean so it can be injected elsewhere (e.g. into JwtAuthFilter)
public class JwtService {

    private final SecretKey key;
    private final long expirationMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,   //pulled from application.properties
            @Value("${jwt.expiration-ms}") long expirationMs   //pulled from application.properties
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());   //turn the raw secret string into a usable signing key
        this.expirationMs = expirationMs;
    }

    public String generateToken(UUID userId, boolean isAdmin) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);   //token becomes invalid after this point

        return Jwts.builder()
                .subject(userId.toString())   //who this token identifies
                .claim("isAdmin", isAdmin)   //extra info carried inside the token
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)   //sign it so it can't be tampered with
                .compact();   //turn it into the final token string
    }

    public Claims verifyToken(String token) {
        Jws<Claims> parsed = Jwts.parser()
                .verifyWith(key)   //checks the signature matches; throws if the token was tampered with or expired
                .build()
                .parseSignedClaims(token);

        return parsed.getPayload();   //the decoded contents (subject, isAdmin, etc.)
    }
}