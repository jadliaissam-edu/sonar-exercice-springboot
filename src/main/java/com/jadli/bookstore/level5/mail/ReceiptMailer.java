package com.jadli.bookstore.level5.mail;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ReceiptMailer {

    private static final Logger log = LoggerFactory.getLogger(ReceiptMailer.class);

    @Value("${bookstore.mail.sender}")
    private static String sender;

    @Value("bookstore.name")
    private String shopName;

    @Async
    public String sendReceipt(Long orderId, String recipient) {
        log.info("sending receipt for order {} to {} from {}", orderId, recipient, sender);
        return "queued:" + orderId;
    }

    public int sendAll(List<Long> orderIds, String recipient) {
        int sent = 0;
        for (Long orderId : orderIds) {
            if (this.sendReceipt(orderId, recipient) != null) {
                sent++;
            }
        }
        return sent;
    }

    public String signature() {
        return "-- " + shopName;
    }
}
