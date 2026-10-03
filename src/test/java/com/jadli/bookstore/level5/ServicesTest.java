package com.jadli.bookstore.level5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jadli.bookstore.domain.Book;
import com.jadli.bookstore.domain.Payment;
import com.jadli.bookstore.level5.jobs.NightlyReconciliationJob;
import com.jadli.bookstore.level5.mail.ReceiptMailer;
import com.jadli.bookstore.level5.payments.RefundService;
import com.jadli.bookstore.level5.recommend.RecommendationService;
import com.jadli.bookstore.level5.recommend.SessionBasket;
import com.jadli.bookstore.repository.BookRepository;
import com.jadli.bookstore.repository.PaymentRepository;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServicesTest {

    @Mock
    private PaymentRepository payments;

    @Mock
    private BookRepository books;

    @Test
    void refundMarksPaidPayments() throws IOException {
        Payment paid = new Payment(1L, 10.0, "CARD", "PAID");
        Payment failed = new Payment(1L, 5.0, "CARD", "FAILED");
        when(payments.findByOrderId(1L)).thenReturn(List.of(paid, failed));
        assertEquals(1, new RefundService(payments).refundOrder(1L));
        assertEquals("REFUNDED", paid.getStatus());
    }

    @Test
    void jobSumsPayments() {
        NightlyReconciliationJob job = new NightlyReconciliationJob(payments);
        BigDecimal total = job.totalOf(List.of(new Payment(1L, 10.5, "CARD", "PAID"), new Payment(2L, 4.5, "CARD", "PAID")));
        assertEquals(new BigDecimal("15.00"), total);
    }

    @Test
    void reconcileSurvivesRepositoryErrors() {
        when(payments.findAll()).thenThrow(new IllegalStateException("db down"));
        new NightlyReconciliationJob(payments).reconcile();
        verify(payments, times(1)).findAll();
    }

    @Test
    void markProcessedReportsZeroBecauseOfFinally() {
        Payment payment = new Payment(1L, 1.0, "CARD", "PAID");
        when(payments.findById(9L)).thenReturn(Optional.of(payment));
        assertEquals(0, new NightlyReconciliationJob(payments).markProcessed(9L));
        assertEquals("RECONCILED", payment.getStatus());
    }

    @Test
    void mailerCountsSentReceipts() {
        assertEquals(2, new ReceiptMailer().sendAll(List.of(1L, 2L), "alice@example.com"));
    }

    @Test
    void featuredReturnsRequestedAmount() {
        when(books.findAll()).thenReturn(List.of(new Book(), new Book(), new Book()));
        assertEquals(2, new RecommendationService(books, new SessionBasket()).featured(2).size());
    }

}
