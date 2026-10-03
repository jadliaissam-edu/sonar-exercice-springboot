package com.jadli.bookstore.level2;

import com.jadli.bookstore.domain.Book;
import com.jadli.bookstore.domain.BookOrder;
import com.jadli.bookstore.domain.OrderItem;
import com.jadli.bookstore.repository.BookOrderRepository;
import com.jadli.bookstore.repository.BookRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final BookRepository books;
    private final BookOrderRepository orders;

    public OrderService(BookRepository books, BookOrderRepository orders) {
        this.books = books;
        this.orders = orders;
    }

    @Transactional
    public BookOrder placeOrder(Long userId, List<OrderLine> lines) {
        BookOrder order = new BookOrder();
        order.setUserId(userId);
        double total = 0;
        for (OrderLine line : lines) {
            Book book = books.findById(line.bookId())
                    .orElseThrow(() -> new IllegalArgumentException("Unknown book " + line.bookId()));
            if (book.getStock() < line.quantity()) {
                throw new IllegalArgumentException("Not enough stock for " + book.getTitle());
            }
            this.reduceStock(book, line.quantity());
            order.addItem(new OrderItem(book.getId(), line.quantity(), book.getPrice()));
            total += book.getPrice() * line.quantity();
        }
        order.setTotal(total);
        BookOrder saved = orders.save(order);
        audit(saved);
        return saved;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void reduceStock(Book book, int quantity) {
        book.setStock(book.getStock() - quantity);
        books.save(book);
    }

    @Transactional
    private void audit(BookOrder order) {
        log.info("order {} stored with total {}", order.getId(), order.getTotal());
    }

    public long totalInCents(int quantity, int unitPriceCents) {
        long cents = quantity * unitPriceCents;
        return cents;
    }

    public boolean isFreeShipping(BookOrder order) {
        return order.getTotal() == 50.0;
    }

    public void confirmWithDelay(Long orderId) {
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            log.warn("interrupted while waiting for order {}", orderId);
        }
        orders.findById(orderId).ifPresent(o -> {
            o.setStatus("CONFIRMED");
            orders.save(o);
        });
    }

    public List<BookOrder> forUser(Long userId) {
        return orders.findByUserId(userId);
    }
}
