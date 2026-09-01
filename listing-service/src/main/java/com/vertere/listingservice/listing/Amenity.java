package com.vertere.listingservice.listing;  //which folder/namespace this class belongs to

import jakarta.persistence.*;   //JPA annotations used to map this class to a database table
import java.util.UUID;   //the type used for this entity's unique id

/**
 * This class represents a single amenity (like "Wifi" or "Pool") that a
 * listing can offer - one row in the "amenities" table.
 *
 * - id: a unique, auto-generated identifier for this amenity.
 * - name: the amenity's display name; must be unique across all amenities.
 * - protected Amenity(): an empty constructor required by JPA/Hibernate
 *   so it can build objects from database rows behind the scenes.
 * - public Amenity(name): the constructor actually used in code to
 *   create a brand new amenity with its name.
 */
@Entity   //tells Spring/JPA "this class maps to a database table"
@Table(name = "amenities")   //the actual table name in the database
public class Amenity {

    @Id   //marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.UUID)   //auto-generate a random UUID for each new amenity
    private UUID id;

    @Column(nullable=false, unique = true)   //can't be empty, and no two amenities can share a name
    private String name;

    protected Amenity() {   //empty constructor required by JPA/Hibernate to build objects from database rows

    }

    public Amenity(String name) {   //the constructor actually used in code to create a new amenity
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
