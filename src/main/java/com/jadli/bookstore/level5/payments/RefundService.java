package com.jadli.bookstore.level5.payments;

import com.jadli.bookstore.domain.Payment;
import com.jadli.bookstore.repository.PaymentRepository;
import java.io.IOException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefundService {

    private final PaymentRepository payments;

    public RefundService(PaymentRepository payments) {
        this.payments = payments;
    }

    @Transactional
    public int refundOrder(Long orderId) throws IOException {
        List<Payment> list = payments.findByOrderId(orderId);
        if (list.isEmpty()) {
            throw new IOException("no payments for order " + orderId);
        }
        int refunded = 0;
        for (Payment payment : list) {
            if ("PAID".equals(payment.getStatus())) {
                payment.setStatus("REFUNDED");
                payments.save(payment);
                refunded++;
            } else if ("REFUND_PENDING".equals(payment.getStatus())) {
                payment.setStatus("REFUNDED");
                payments.save(payment);
                refunded++;
            }
        }
        return refunded;
    }
}
