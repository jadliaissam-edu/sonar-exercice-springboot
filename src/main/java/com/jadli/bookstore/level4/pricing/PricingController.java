package com.jadli.bookstore.level4.pricing;

import com.jadli.bookstore.repository.BookRepository;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pricing")
public class PricingController {

    private final PricingService pricingService;
    private final BookRepository bookRepository;

    public PricingController(PricingService pricingService, BookRepository bookRepository) {
        this.pricingService = pricingService;
        this.bookRepository = bookRepository;
    }

    @GetMapping("/quote/{bookId}")
    public ResponseEntity<Map<String, Object>> quote(@PathVariable Long bookId,
                                                     @RequestParam(defaultValue = "1") int quantity,
                                                     @RequestParam(required = false) String tier,
                                                     @RequestParam(required = false) String coupon,
                                                     @RequestParam(defaultValue = "LT") String region,
                                                     @RequestParam(defaultValue = "false") boolean giftWrap,
                                                     @RequestParam(defaultValue = "false") boolean express) {
        return bookRepository.findById(bookId)
                .map(book -> ResponseEntity.ok(Map.<String, Object>of(
                        "bookId", bookId,
                        "price", pricingService.calculatePrice(book, quantity, tier, coupon, region,
                                giftWrap, express, LocalDate.now()))))
                .orElse(ResponseEntity.notFound().build());
    }
}
