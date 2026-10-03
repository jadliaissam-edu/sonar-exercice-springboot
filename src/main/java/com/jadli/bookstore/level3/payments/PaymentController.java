package com.jadli.bookstore.level3.payments;

import com.jadli.bookstore.domain.Payment;
import com.jadli.bookstore.repository.PaymentRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentGatewayClient gateway;
    private final PaymentRepository payments;

    public PaymentController(PaymentGatewayClient gateway, PaymentRepository payments) {
        this.gateway = gateway;
        this.payments = payments;
    }

    @PostMapping("/charge")
    public Payment charge(@RequestParam Long orderId, @RequestParam double amount) {
        String result = gateway.charge(orderId, amount);
        return payments.save(new Payment(orderId, amount, "CARD", result));
    }

    @GetMapping("/status")
    public String remoteStatus(@RequestParam("url") String url) {
        return gateway.fetchStatus(url);
    }

    @GetMapping("/order/{orderId}")
    public List<Payment> forOrder(@PathVariable Long orderId) {
        return payments.findByOrderId(orderId);
    }

    @PostMapping("/refund")
    public Payment refund(@RequestParam Long orderId, @RequestParam double amount) {
        String result = gateway.refund(orderId, amount);
        return payments.save(new Payment(orderId, -amount, "CARD", result));
    }

    @GetMapping("/gateway")
    public String gateway() {
        return gateway.isGatewayUp() ? "UP" : "DOWN";
    }
}
