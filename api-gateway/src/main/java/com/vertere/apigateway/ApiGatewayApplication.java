package com.vertere.apigateway;  //which folder/namespace this class belongs to

import org.springframework.boot.SpringApplication;   //starts up the Spring Boot application
import org.springframework.boot.autoconfigure.SpringBootApplication;   //auto-configures Spring based on what's on the classpath

/**
 * This is the entry point that starts up the whole api-gateway app - the
 * single front door clients talk to, which then forwards requests to
 * whichever backend microservice actually handles them.
 *
 * - main: boots the Spring application, which wires up RequestRouter and
 *   GatewayController and starts listening for requests.
 */
@SpringBootApplication   //marks this as the main Spring Boot config/entry point class
public class ApiGatewayApplication {

	public static void main(String[] args) {   //the very first method that runs when the app starts
		SpringApplication.run(ApiGatewayApplication.class, args);   //hands control over to Spring to boot everything up
	}

}
