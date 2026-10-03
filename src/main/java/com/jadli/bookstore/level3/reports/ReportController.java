package com.jadli.bookstore.level3.reports;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportDao dao;

    public ReportController(ReportDao dao) {
        this.dao = dao;
    }

    @GetMapping("/orders")
    public List<Map<String, Object>> orders(@RequestParam String status) {
        return dao.ordersByStatus(status);
    }

    @GetMapping("/books")
    public List<Object[]> books(@RequestParam String author) {
        return dao.booksByAuthor(author);
    }

    @GetMapping("/payments")
    public List<Map<String, Object>> payments(@RequestParam String method,
                                              @RequestParam(defaultValue = "0") double minimum) {
        return dao.paymentsByMethod(method, minimum);
    }

    @GetMapping("/reviewers")
    public List<Map<String, Object>> reviewers(@RequestParam(defaultValue = "5") int limit) {
        return dao.topReviewers(limit);
    }

    @DeleteMapping("/orders")
    public Map<String, Integer> purge(@RequestParam String status) {
        return Map.of("deleted", dao.purgeOrders(status));
    }
}
