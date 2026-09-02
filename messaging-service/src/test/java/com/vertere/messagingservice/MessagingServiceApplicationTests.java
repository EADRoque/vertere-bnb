package com.vertere.messagingservice;  //which folder/namespace this class belongs to

import org.junit.jupiter.api.Test;   //marks a method as a test case
import org.springframework.boot.test.context.SpringBootTest;   //boots the full Spring application for this test

/**
 * This is a basic sanity-check test - it just confirms the whole Spring
 * application can start up successfully with all its beans wired
 * together (database connection, entities, repositories, etc.). It
 * doesn't test any actual business logic.
 *
 * - contextLoads: empty on purpose - if Spring fails to start, this test
 *   fails automatically before the empty body would even matter.
 */
@SpringBootTest   //starts the real application context, not just a slice of it
class MessagingServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
