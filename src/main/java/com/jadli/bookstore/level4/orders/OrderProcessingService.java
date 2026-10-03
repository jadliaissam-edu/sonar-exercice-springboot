package com.jadli.bookstore.level4.orders;

import com.jadli.bookstore.domain.AppUser;
import com.jadli.bookstore.domain.Book;
import com.jadli.bookstore.domain.BookOrder;
import com.jadli.bookstore.domain.OrderItem;
import com.jadli.bookstore.domain.Payment;
import com.jadli.bookstore.domain.Review;
import com.jadli.bookstore.level2.OrderLine;
import com.jadli.bookstore.level4.billing.InvoiceService;
import com.jadli.bookstore.level4.billing.ShippingLabelService;
import com.jadli.bookstore.level4.pricing.DiscountService;
import com.jadli.bookstore.level4.pricing.PricingService;
import com.jadli.bookstore.repository.AppUserRepository;
import com.jadli.bookstore.repository.BookOrderRepository;
import com.jadli.bookstore.repository.BookRepository;
import com.jadli.bookstore.repository.PaymentRepository;
import com.jadli.bookstore.repository.ReviewRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderProcessingService {

    private static final Logger log = LoggerFactory.getLogger(OrderProcessingService.class);

    private final BookRepository bookRepository;
    private final BookOrderRepository orderRepository;
    private final AppUserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final ReviewRepository reviewRepository;
    private final PricingService pricingService;
    private final DiscountService discountService;
    private final InvoiceService invoiceService;
    private final ShippingLabelService shippingLabelService;

    private int maxItemsPerOrder = 50;
    private int maxQuantityPerLine = 20;
    private double freeShippingThreshold = 60.0;
    private double fraudLimit = 2500.0;
    private int processedToday;
    private int rejectedToday;
    private String lastError;

    public OrderProcessingService(BookRepository bookRepository, BookOrderRepository orderRepository,
                                  AppUserRepository userRepository, PaymentRepository paymentRepository,
                                  ReviewRepository reviewRepository, PricingService pricingService,
                                  DiscountService discountService, InvoiceService invoiceService,
                                  ShippingLabelService shippingLabelService) {
        this.bookRepository = bookRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.reviewRepository = reviewRepository;
        this.pricingService = pricingService;
        this.discountService = discountService;
        this.invoiceService = invoiceService;
        this.shippingLabelService = shippingLabelService;
    }

    public BookOrder checkout(Long userId, List<OrderLine> lines, String couponCode, String region, String tier,
                              boolean giftWrap, boolean express, String note) {
        AppUser user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            lastError = "unknown user";
            rejectedToday++;
            throw new IllegalArgumentException("Unknown user " + userId);
        }
        if (lines == null || lines.isEmpty()) {
            lastError = "empty basket";
            rejectedToday++;
            throw new IllegalArgumentException("Basket is empty");
        }
        if (lines.size() > maxItemsPerOrder) {
            lastError = "too many lines";
            rejectedToday++;
            throw new IllegalArgumentException("Too many lines");
        }
        BookOrder order = new BookOrder();
        order.setUserId(userId);
        double total = 0;
        int totalQuantity = 0;
        for (OrderLine line : lines) {
            Book book = bookRepository.findById(line.bookId()).orElse(null);
            if (book != null) {
                if (line.quantity() > 0) {
                    if (line.quantity() <= maxQuantityPerLine) {
                        if (book.getStock() >= line.quantity()) {
                            double linePrice = pricingService.calculatePrice(book, line.quantity(), tier, couponCode,
                                    region, false, false, LocalDate.now());
                            if (user.getRole() != null && user.getRole().equals("STAFF")) {
                                linePrice = linePrice - discountService.bestOf(linePrice);
                            } else if (note != null && note.contains("gift")) {
                                linePrice = linePrice - discountService.loyaltyDiscount(linePrice);
                            }
                            book.setStock(book.getStock() - line.quantity());
                            bookRepository.save(book);
                            OrderItem item = new OrderItem(book.getId(), line.quantity(), linePrice / line.quantity());
                            order.addItem(item);
                            total += linePrice;
                            totalQuantity += line.quantity();
                        } else {
                            lastError = "not enough stock";
                            rejectedToday++;
                            throw new IllegalArgumentException("Not enough stock for " + book.getTitle());
                        }
                    } else {
                        lastError = "quantity too high";
                        rejectedToday++;
                        throw new IllegalArgumentException("Quantity too high for " + book.getTitle());
                    }
                } else {
                    lastError = "invalid quantity";
                    rejectedToday++;
                    throw new IllegalArgumentException("Invalid quantity");
                }
            } else {
                lastError = "unknown book";
                rejectedToday++;
                throw new IllegalArgumentException("Unknown book " + line.bookId());
            }
        }
        if (total < freeShippingThreshold && !express) {
            total += 3.99;
        }
        if (giftWrap) {
            total += totalQuantity * 1.5;
        }
        if (total > fraudLimit) {
            order.setStatus("REVIEW");
        } else {
            order.setStatus("PAID");
        }
        order.setTotal(total);
        BookOrder saved = orderRepository.save(order);
        paymentRepository.save(new Payment(saved.getId(), total, "CARD", order.getStatus()));
        processedToday++;
        notifyUser(user, "Order " + saved.getId() + " received");
        return saved;
    }

    public String cancelOrder(Long orderId, String reason) {
        BookOrder order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return "NOT_FOUND";
        }
        if (order.getStatus().equals("SHIPPED")) {
            return "TOO_LATE";
        }
        if (order.getStatus().equals("CANCELLED")) {
            return "ALREADY_CANCELLED";
        }
        for (OrderItem item : order.getItems()) {
            Book book = bookRepository.findById(item.getBookId()).orElse(null);
            if (book != null) {
                book.setStock(book.getStock() + item.getQuantity());
                bookRepository.save(book);
            }
        }
        order.setStatus("CANCELLED");
        orderRepository.save(order);
        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        for (Payment p : payments) {
            if (p.getStatus().equals("PAID")) {
                p.setStatus("REFUND_PENDING");
                paymentRepository.save(p);
            }
        }
        log.info("order {} cancelled: {}", orderId, reason);
        return "CANCELLED";
    }

    public Map<String, Object> monthlyReport(int year, int month) {
        Map<String, Object> report = new HashMap<>();
        List<BookOrder> all = orderRepository.findAll();
        double revenue = 0;
        int count = 0;
        int cancelled = 0;
        Map<Long, Integer> soldPerBook = new HashMap<>();
        for (BookOrder o : all) {
            LocalDateTime created = o.getCreatedAt();
            if (created.getYear() == year && created.getMonthValue() == month) {
                if (o.getStatus().equals("CANCELLED")) {
                    cancelled++;
                } else {
                    revenue += o.getTotal();
                    count++;
                    for (OrderItem item : o.getItems()) {
                        soldPerBook.merge(item.getBookId(), item.getQuantity(), Integer::sum);
                    }
                }
            }
        }
        Long bestSeller = null;
        int best = 0;
        for (Map.Entry<Long, Integer> e : soldPerBook.entrySet()) {
            if (e.getValue() > best) {
                best = e.getValue();
                bestSeller = e.getKey();
            }
        }
        report.put("revenue", revenue);
        report.put("orders", count);
        report.put("cancelled", cancelled);
        report.put("bestSeller", bestSeller);
        report.put("processedToday", processedToday);
        report.put("rejectedToday", rejectedToday);
        return report;
    }

    public List<String> reconcilePayments() {
        List<String> problems = new ArrayList<>();
        for (BookOrder order : orderRepository.findAll()) {
            List<Payment> payments = paymentRepository.findByOrderId(order.getId());
            if (payments.isEmpty()) {
                problems.add("order " + order.getId() + " has no payment");
            } else {
                double paid = 0;
                for (Payment p : payments) {
                    if (!p.getStatus().equals("FAILED")) {
                        paid += p.getAmount();
                    }
                }
                if (Math.abs(paid - order.getTotal()) > 0.01) {
                    problems.add("order " + order.getId() + " paid " + paid + " expected " + order.getTotal());
                }
            }
        }
        return problems;
    }

    public int loyaltyPoints(Long userId) {
        int points = 0;
        for (BookOrder order : orderRepository.findByUserId(userId)) {
            if (!order.getStatus().equals("CANCELLED")) {
                points += (int) (order.getTotal() / 10);
            }
        }
        for (Review review : reviewRepository.findByUserId(userId)) {
            points += review.getComment() != null && review.getComment().length() > 20 ? 5 : 1;
        }
        return points;
    }

    public String documentsFor(Long orderId, String street, String city, String postCode, String country) {
        BookOrder order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return "";
        }
        AppUser user = userRepository.findById(order.getUserId()).orElse(null);
        if (user == null) {
            return "";
        }
        return invoiceService.buildInvoice(order, user, street, city, postCode, country)
                + shippingLabelService.buildLabel(order, user, street, city, postCode, country);
    }

    private void notifyUser(AppUser user, String message) {
        log.info("notify {}: {}", user.getEmail(), message);
    }

    public String getLastError() {
        return lastError;
    }
}
