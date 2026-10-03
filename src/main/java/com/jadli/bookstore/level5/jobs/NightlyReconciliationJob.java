package com.jadli.bookstore.level5.jobs;

import com.jadli.bookstore.domain.Payment;
import com.jadli.bookstore.repository.PaymentRepository;
import java.math.BigDecimal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NightlyReconciliationJob {

    private static final Logger log = LoggerFactory.getLogger(NightlyReconciliationJob.class);

    private final PaymentRepository payments;

    public NightlyReconciliationJob(PaymentRepository payments) {
        this.payments = payments;
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void reconcile() {
        try {
            List<Payment> all = payments.findAll();
            log.info("reconciling {} payments", all.size());
            totalOf(all);
        } catch (Throwable t) {
            log.error("reconciliation failed", t);
        }
    }

    public BigDecimal totalOf(List<Payment> list) {
        BigDecimal total = BigDecimal.ZERO;
        for (Payment p : list) {
            total = total.add(BigDecimal.valueOf(p.getAmount()));
        }
        return total.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public int markProcessed(Long paymentId) {
        try {
            Payment p = payments.findById(paymentId).orElseThrow(() -> new IllegalStateException("missing payment"));
            p.setStatus("RECONCILED");
            payments.save(p);
            return 1;
        } finally {
            log.debug("payment {} handled", paymentId);
            return 0;
        }
    }

    public void failHard(String reason) {
        throw new RuntimeException("reconciliation aborted: " + reason);
    }

    public void runNow() {
        Thread worker = new Thread(this::reconcile);
        worker.run();
    }
}
