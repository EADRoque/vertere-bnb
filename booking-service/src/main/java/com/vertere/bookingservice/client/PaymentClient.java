package com.vertere.bookingservice.client;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PaymentClient {

    private final RestClient restClient;

    public PaymentClient() {
        var requestFactory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(3000);
        requestFactory.setReadTimeout(3000);

        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8084")
                .requestFactory(requestFactory)
                .build();
    }

    public boolean charge(UUID bookingId, BigDecimal amount) {
        ChargeRequestDto request = new ChargeRequestDto(bookingId, amount);

        ChargeResponseDto response = restClient.post()
                .uri("/payments")
                .body(request)
                .retrieve()
                .body(ChargeResponseDto.class);

        return "SUCCEEDED".equals(response.status());
    }

    private record ChargeRequestDto(UUID bookingId, BigDecimal amount) {}
    private record ChargeResponseDto(String status) {}

}