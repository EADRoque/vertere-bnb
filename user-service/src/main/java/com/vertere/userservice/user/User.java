package com.vertere.userservice.user;  //which folder/namespace this class belongs to

import jakarta.persistence.*;   //JPA annotations used to map this class to a database table (@Entity, @Table, @Id, etc.)
import java.time.Instant;   //represents a precise point in time, used for createdAt
import java.util.UUID;   //represents a universally unique ID, used for the id field

/**
 * This class describes what a "user" looks like in our app, and each
 * user is stored as one row in the "users" table in the database.
 *
 * - id: a unique code automatically assigned to each user, like an ID number.
 * - email: the user's email, used to log in. No two users can share one.
 * - passwordHash: the user's password, but scrambled/encrypted for safety
 *   so it's never stored as plain, readable text.
 * - fullName: the user's name.
 * - phone / avatarUrl: optional info the user doesn't have to fill in.
 * - admin: true/false switch for whether this user has admin powers.
 * - createdAt: the date and time the account was created. It gets set
 *   once and is never changed afterward.
 *
 * The empty constructor near the top exists only because the database
 * tool (Hibernate) needs a way to build a blank User behind the scenes.
 * When we create users ourselves in our own code, we use the other
 * constructor instead, which makes sure the important fields are filled in.
 */
@Entity   //tells Hibernate/JPA "this class represents a database table"
@Table(name = "users")   //that table is specifically named "users"
public class User {

    @Id   //marks id as the primary key
    @GeneratedValue(strategy = GenerationType.UUID)   //database auto-generates a random UUID for each new row
    private UUID id;

    @Column(nullable = false, unique = true)   //email can't be empty, and no two rows can share the same value
    private String email;

    @Column(name = "password_hash", nullable = false)   //maps to a differently-named column, required
    private String passwordHash;

    @Column(name = "full_name", nullable = false)   //maps to column full_name, required
    private String fullName;

    private String phone;   //no annotation, so it's optional and column name matches the field name

    @Column(name = "avatar_url")   //maps to column avatar_url, optional by default
    private String avatarUrl;

    @Column(name = "is_admin", nullable = false)   //maps to column is_admin, required
    private boolean admin = false;   //defaults to false (not an admin) unless changed

    @Column(name = "created_at", nullable = false, updatable = false)   //required, and never updated once saved
    private Instant createdAt = Instant.now();   //captures the current timestamp when the object is created

    protected User() {   //empty constructor Hibernate needs to build User objects behind the scenes

    }

    public User(String email, String passwordHash, String fullName) {   //constructor our own code should use
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
    }   //ensures every new User we create has these three required values set

    public UUID getId() {   //returns the id (no setId - it's auto-generated and shouldn't change)
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean isAdmin() {   //"is" prefix instead of "get", Java convention for boolean getters
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public Instant getCreatedAt() {   //no setCreatedAt - it's set once and never changed
        return createdAt;
    }
}
