package com.jadli.bookstore.level5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jadli.bookstore.domain.Book;
import com.jadli.bookstore.level5.context.UserContext;
import com.jadli.bookstore.level5.legacy.CartSnapshot;
import com.jadli.bookstore.level5.legacy.LegacyDateUtil;
import com.jadli.bookstore.level5.pricing.CurrencyRateProvider;
import com.jadli.bookstore.level5.stats.SalesReport;
import com.jadli.bookstore.level5.stats.SalesStatsService;
import java.util.Calendar;
import java.util.Date;
import org.junit.jupiter.api.Test;

class ConcurrencyHelpersTest {

    @Test
    void salesAreCountedAndTrimmed() {
        SalesStatsService stats = new SalesStatsService();
        for (int i = 0; i < 30; i++) {
            stats.recordSale("title-" + i);
        }
        assertEquals(30, stats.getTotalSales());
        assertEquals(25, stats.recent().size());
        assertEquals("title-5", stats.recent().get(0));
    }

    @Test
    void resetClearsCounters() {
        SalesStatsService stats = new SalesStatsService();
        stats.recordSale("x");
        stats.reset();
        assertEquals(0, stats.getTotalSales());
        assertTrue(stats.recent().isEmpty());
    }

    @Test
    void todayHasIsoFormat() {
        assertTrue(new SalesStatsService().today().matches("\\d{4}-\\d{2}-\\d{2}"));
    }

    @Test
    void reportDescribesYear() {
        assertTrue(new SalesReport(2024).describe().contains("Sales 2024"));
    }

    @Test
    void singletonIsShared() {
        assertSame(CurrencyRateProvider.getInstance(), CurrencyRateProvider.getInstance());
        assertEquals(1.08, CurrencyRateProvider.getInstance().convert(1.0, "USD"), 0.0001);
    }

    @Test
    void unknownCurrencyIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> CurrencyRateProvider.getInstance().convert(1.0, "XXX"));
    }

    @Test
    void userContextKeepsName() {
        assertNull(UserContext.get());
        UserContext.set("alice");
        assertEquals("alice", UserContext.get());
        UserContext.set(null);
    }

    @Test
    void legacyDatesStillWork() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2020, Calendar.JUNE, 15);
        Date date = calendar.getTime();
        assertEquals(2020, LegacyDateUtil.yearOf(date));
        assertEquals(2021, LegacyDateUtil.yearOf(LegacyDateUtil.firstDayOf(2021)));
    }

    @Test
    void snapshotKeepsOwner() {
        CartSnapshot snapshot = new CartSnapshot("alice", new Book());
        assertEquals("alice", snapshot.getOwner());
        assertNotNull(snapshot.getLastAdded());
    }
}
