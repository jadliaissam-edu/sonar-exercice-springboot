package com.jadli.bookstore.level2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.jadli.bookstore.domain.Review;
import com.jadli.bookstore.repository.ReviewRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviews;

    @Test
    void averageOfNoReviewsIsZero() {
        when(reviews.findByBookId(1L)).thenReturn(List.of());
        assertEquals(0.0, new ReviewService(reviews).averageRating(1L));
    }

    @Test
    void averageIsRounded() {
        when(reviews.findByBookId(1L)).thenReturn(List.of(new Review(1L, 1L, 5, ""), new Review(1L, 2L, 4, ""), new Review(1L, 3L, 4, "")));
        assertEquals(4.33, new ReviewService(reviews).averageRating(1L));
    }

    @Test
    void summaryComparesByBookAndCount() {
        ReviewSummary a = new ReviewSummary(1L, 2, 4.5);
        ReviewSummary b = new ReviewSummary(1L, 2, 4.0);
        assertEquals(a, b);
        assertNotEquals(a, new ReviewSummary(2L, 2, 4.5));
    }

    @Test
    void reviewIsSaved() {
        when(reviews.save(any(Review.class))).thenAnswer(i -> i.getArgument(0));
        Review saved = new ReviewService(reviews).addReview(1L, 2L, 5, "great");
        assertEquals(5, saved.getRating());
    }

    @Test
    void userHasNotReviewedUnknownBook() {
        when(reviews.findByUserId(1L)).thenReturn(List.of(new Review(3L, 1L, 5, "")));
        assertFalse(new ReviewService(reviews).hasReviewed(1L, 3L));
    }
}
