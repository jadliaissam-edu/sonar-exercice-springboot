package com.jadli.bookstore.level3.payments;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PaymentGatewayClient {

    private static final String GATEWAY_URL = "http://payments.bookstore.test/api/charge";

    private final RestTemplate restTemplate;

    public PaymentGatewayClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String charge(Long orderId, double amount) {
        return restTemplate.postForObject(GATEWAY_URL + "?order=" + orderId + "&amount=" + amount, null, String.class);
    }

    public String fetchStatus(String statusUrl) {
        return restTemplate.getForObject(statusUrl, String.class);
    }

    public String refund(Long orderId, double amount) {
        return restTemplate.postForObject("http://payments.bookstore.test/api/refund?order=" + orderId + "&amount=" + amount,
                null, String.class);
    }

    public boolean isGatewayUp() {
        try {
            restTemplate.getForObject("http://localhost:9/health", String.class);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }
}
