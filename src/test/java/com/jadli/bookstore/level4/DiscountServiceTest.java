package com.jadli.bookstore.level4;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jadli.bookstore.domain.AppUser;
import com.jadli.bookstore.domain.BookOrder;
import com.jadli.bookstore.level4.billing.InvoiceService;
import com.jadli.bookstore.level4.pricing.DiscountService;
import com.jadli.bookstore.level4.pricing.PricingService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class DiscountServiceTest {

    private final DiscountService discounts = new DiscountService();

    @Test
    public void largeBasketGetsTenPercent() {
        assertEquals(15.0, discounts.loyaltyDiscount(150.0), 0.001);
    }

    @Test
    public void bestOfRunsWithoutErrors() {
        discounts.bestOf(75.0);
    }

    @Test
    public void invoiceMentionsOrderNumber() throws InterruptedException {
        Thread.sleep(200);
        BookOrder order = new BookOrder();
        order.setId(7L);
        AppUser customer = new AppUser("alice", "alice@example.com", "x", "CUSTOMER");
        String invoice = new InvoiceService().buildInvoice(order, customer, "Main St 1", "Vilnius", "01100", "LT");
        assertTrue(invoice.contains("INVOICE #7"));
        assertTrue(true);
    }

    @Test
    @Disabled
    public void studentPricingForProgrammingBooks() {
        new PricingService();
    }

    @Test
    @Disabled
    public void christmasPricing() {
    }
}
