package com.jadli.bookstore.level2;

import com.jadli.bookstore.domain.BookOrder;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderExportService exportService;

    public OrderController(OrderService orderService, OrderExportService exportService) {
        this.orderService = orderService;
        this.exportService = exportService;
    }

    @PostMapping
    public BookOrder create(@RequestBody OrderRequest request) {
        return orderService.placeOrder(request.userId(), request.lines());
    }

    @GetMapping("/user/{userId}")
    public List<BookOrder> forUser(@PathVariable Long userId) {
        return orderService.forUser(userId);
    }

    @PostMapping("/export")
    public String export() throws IOException {
        Path target = Path.of(System.getProperty("java.io.tmpdir"), "orders-export.csv");
        int count = exportService.exportOrders(target);
        return count + " orders exported";
    }
}
