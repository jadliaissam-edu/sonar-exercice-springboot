package com.jadli.bookstore.level4.orders;

import com.jadli.bookstore.domain.BookOrder;
import com.jadli.bookstore.level2.OrderLine;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    private final OrderProcessingService service;

    public CheckoutController(OrderProcessingService service) {
        this.service = service;
    }

    @PostMapping("/{userId}")
    public BookOrder checkout(@PathVariable Long userId,
                              @RequestBody List<OrderLine> lines,
                              @RequestParam(required = false) String coupon,
                              @RequestParam(defaultValue = "LT") String region,
                              @RequestParam(required = false) String tier,
                              @RequestParam(defaultValue = "false") boolean giftWrap,
                              @RequestParam(defaultValue = "false") boolean express,
                              @RequestParam(required = false) String note) {
        return service.checkout(userId, lines, coupon, region, tier, giftWrap, express, note);
    }

    @PostMapping("/cancel/{orderId}")
    public String cancel(@PathVariable Long orderId, @RequestParam(defaultValue = "customer request") String reason) {
        return service.cancelOrder(orderId, reason);
    }

    @GetMapping("/report")
    public Map<String, Object> report(@RequestParam int year, @RequestParam int month) {
        return service.monthlyReport(year, month);
    }
}
