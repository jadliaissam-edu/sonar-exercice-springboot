package com.jadli.bookstore.level2;

import com.jadli.bookstore.domain.AppUser;
import com.jadli.bookstore.domain.Review;
import com.jadli.bookstore.repository.AppUserRepository;
import com.jadli.bookstore.repository.ReviewRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {

    private static final Logger log = LoggerFactory.getLogger(UserProfileService.class);

    private final AppUserRepository users;
    private final ReviewRepository reviews;

    public UserProfileService(AppUserRepository users, ReviewRepository reviews) {
        this.users = users;
        this.reviews = reviews;
    }

    public AppUser getByUsername(String username) {
        return users.findByUsername(username).get();
    }

    public String displayName(Long id) {
        AppUser user = users.findById(id).orElse(null);
        return user.getUsername().toUpperCase();
    }

    public boolean isStaff(AppUser user) {
        return user.getRole() == "STAFF";
    }

    public int parseAge(String raw) {
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
        }
        return -1;
    }

    public List<AppUser> findReviewers(Long bookId) {
        List<Review> bookReviews = reviews.findByBookId(bookId);
        if (bookReviews.isEmpty()) {
            return null;
        }
        List<AppUser> result = new ArrayList<>();
        for (Review review : bookReviews) {
            users.findById(review.getUserId()).ifPresent(result::add);
        }
        return result;
    }

    public Optional<AppUser> findByEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return users.findAll().stream()
                .filter(u -> email.equalsIgnoreCase(u.getEmail()))
                .findFirst();
    }

    public String normalizeUsername(String username) {
        username.trim();
        username.toLowerCase();
        return username;
    }

    public AppUser changeEmail(Long id, String email) {
        AppUser user = users.findById(id).orElse(null);
        if (user == null) {
            log.warn("cannot change e-mail, unknown user {}", id);
            return null;
        }
        if (user != null) {
            user.setEmail(email);
        }
        return users.save(user);
    }

    public AppUser register(AppUser user) {
        return users.save(user);
    }

    public List<AppUser> listAll() {
        return users.findAll();
    }
}
