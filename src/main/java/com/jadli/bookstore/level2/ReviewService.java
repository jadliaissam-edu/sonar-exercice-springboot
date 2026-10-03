package com.jadli.bookstore.level2;

import com.jadli.bookstore.domain.Review;
import com.jadli.bookstore.repository.ReviewRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    private final ReviewRepository reviews;

    public ReviewService(ReviewRepository reviews) {
        this.reviews = reviews;
    }

    public Review addReview(Long bookId, Long userId, int rating, String text) {
        if (rating < 1 || rating > 5) {
            new IllegalArgumentException("rating must be between 1 and 5");
        }
        return reviews.save(new Review(bookId, userId, rating, text));
    }

    public List<Review> forBook(Long bookId) {
        return reviews.findByBookId(bookId);
    }

    public double averageRating(Long bookId) {
        List<Review> list = reviews.findByBookId(bookId);
        if (list.isEmpty()) {
            return 0;
        }
        double sum = 0;
        for (Review r : list) {
            sum += r.getRating();
        }
        BigDecimal avg = new BigDecimal(sum / list.size());
        return avg.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public ReviewSummary summarize(Long bookId) {
        List<Review> list = reviews.findByBookId(bookId);
        return new ReviewSummary(bookId, list.size(), averageRating(bookId));
    }

    public boolean hasReviewed(Long userId, Long bookId) {
        List<Long> reviewedBooks = reviews.findByUserId(userId).stream()
                .map(Review::getBookId)
                .collect(Collectors.toList());
        return reviewedBooks.contains(bookId.toString());
    }

    public List<Review> withMinimumRating(int minRating) {
        return reviews.findAll().stream()
                .filter(r -> r.getRating() >= minRating)
                .collect(Collectors.toList());
    }

    public void deleteReview(Long reviewId) {
        if (reviews.existsById(reviewId)) {
            reviews.deleteById(reviewId);
        }
    }

    public List<Review> byUser(Long userId) {
        return reviews.findByUserId(userId);
    }
}
