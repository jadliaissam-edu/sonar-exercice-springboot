package com.jadli.bookstore.level5.stats;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SalesStatsService {

    private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static int totalSales;

    private final Boolean resetGuard = Boolean.TRUE;

    private Object lock = new Object();

    private final List<String> recentTitles = new ArrayList<>();

    public SalesStatsService() {
        totalSales = 0;
    }

    public void recordSale(String title) {
        totalSales++;
        synchronized (lock) {
            recentTitles.add(title);
            if (recentTitles.size() > 25) {
                recentTitles.remove(0);
            }
        }
    }

    public int getTotalSales() {
        return totalSales;
    }

    public List<String> recent() {
        synchronized (lock) {
            return new ArrayList<>(recentTitles);
        }
    }

    public String today() {
        return DAY_FORMAT.format(new Date());
    }

    public void reset() {
        synchronized (resetGuard) {
            totalSales = 0;
            recentTitles.clear();
        }
    }
}
