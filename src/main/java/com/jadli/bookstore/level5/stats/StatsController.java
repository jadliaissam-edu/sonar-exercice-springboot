package com.jadli.bookstore.level5.stats;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final SalesStatsService stats;

    public StatsController(SalesStatsService stats) {
        this.stats = stats;
    }

    @PostMapping("/sales")
    public Map<String, Object> record(@RequestParam String title) {
        stats.recordSale(title);
        return Map.of("total", stats.getTotalSales(), "day", stats.today());
    }

    @GetMapping("/recent")
    public List<String> recent() {
        return stats.recent();
    }

    @GetMapping("/report/{year}")
    public String report(@org.springframework.web.bind.annotation.PathVariable int year) {
        return new SalesReport(year).describe();
    }
}
