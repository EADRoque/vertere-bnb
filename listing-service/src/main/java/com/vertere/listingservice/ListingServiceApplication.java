package com.vertere.listingservice;  //which folder/namespace this class belongs to

import org.springframework.boot.SpringApplication;   //starts up the Spring Boot application
import org.springframework.boot.autoconfigure.SpringBootApplication;   //auto-configures Spring based on what's on the classpath

/**
 * This is the entry point that starts up the whole listing-service app.
 *
 * - main: boots the Spring application, which wires up all the
 *   components (like Listing, Amenity, and any repositories/controllers)
 *   and starts listening for requests.
 */
@SpringBootApplication   //marks this as the main Spring Boot config/entry point class
public class ListingServiceApplication {

	public static void main(String[] args) {   //the very first method that runs when the app starts
		SpringApplication.run(ListingServiceApplication.class, args);   //hands control over to Spring to boot everything up
	}

}
