package com.jadli.bookstore.repository;

import com.jadli.bookstore.domain.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByBookId(Long bookId);

    List<Review> findByUserId(Long userId);
}
