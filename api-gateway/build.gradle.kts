plugins {
	java
	id("org.springframework.boot") version "4.1.1"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.vertere"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

// This service is a standalone app, not a library other projects depend
// on, so the plain (non-executable) jar Spring Boot's plugin normally
// builds alongside the real one is just noise - disabling it leaves
// exactly one jar in build/libs, which Docker's COPY step needs.
tasks.getByName<Jar>("jar") {
	enabled = false
}
