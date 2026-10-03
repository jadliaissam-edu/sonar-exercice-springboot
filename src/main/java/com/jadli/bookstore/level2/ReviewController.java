package com.jadli.bookstore.level2;

import com.jadli.bookstore.domain.Review;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    @GetMapping("/books/{bookId}")
    public List<Review> forBook(@PathVariable Long bookId) {
        return service.forBook(bookId);
    }

    @GetMapping("/books/{bookId}/average/{precision}")
    public double average(@PathVariable Long bookId) {
        return service.averageRating(bookId);
    }

    @GetMapping("/books/{bookId}/summary")
    public ReviewSummary summary(@PathVariable Long bookId) {
        return service.summarize(bookId);
    }

    @GetMapping("/top")
    public List<Review> top(@RequestParam(required = false) int minRating) {
        return service.withMinimumRating(minRating);
    }

    @PostMapping("/books/{bookId}")
    public Review add(@PathVariable Long bookId,
                      @RequestParam Long userId,
                      @RequestParam int rating,
                      @RequestParam(defaultValue = "") String text) {
        return service.addReview(bookId, userId, rating, text);
    }
}
