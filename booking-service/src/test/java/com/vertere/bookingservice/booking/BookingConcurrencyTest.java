package com.vertere.bookingservice.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
class BookingConcurrencyTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("bookingdb_test")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void onlyOneBookingWins_whenTwoOverlappingRequestsHappenSimultaneously() throws InterruptedException {
        UUID listingId = UUID.randomUUID();

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch startSignal = new CountDownLatch(1);

        Callable<Boolean> attemptBooking = () -> {
            startSignal.await();
            try {
                Booking booking = new Booking(
                        listingId,
                        UUID.randomUUID(),
                        LocalDate.of(2026, 6, 10),
                        LocalDate.of(2026, 6, 15),
                        new BigDecimal("500.00")
                );
                bookingRepository.save(booking);
                return true;
            } catch (Exception e) {
                return false;
            }
        };

        Future<Boolean> result1 = executor.submit(attemptBooking);
        Future<Boolean> result2 = executor.submit(attemptBooking);

        startSignal.countDown();

        boolean success1;
        boolean success2;
        try {
            success1 = result1.get();
            success2 = result2.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        int successCount = (success1 ? 1 : 0) + (success2 ? 1 : 0);
        assertEquals(1, successCount, "Exactly one of the two overlapping bookings should succeed");

        executor.shutdown();
    }

}