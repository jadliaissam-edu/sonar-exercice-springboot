package com.jadli.bookstore.level2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.jadli.bookstore.domain.AppUser;
import com.jadli.bookstore.domain.Review;
import com.jadli.bookstore.repository.AppUserRepository;
import com.jadli.bookstore.repository.ReviewRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private AppUserRepository users;

    @Mock
    private ReviewRepository reviews;

    private UserProfileService service() {
        return new UserProfileService(users, reviews);
    }

    @Test
    void findsUserByName() {
        when(users.findByUsername("alice")).thenReturn(Optional.of(new AppUser("alice", "a@example.com", "x", "CUSTOMER")));
        assertEquals("alice", service().getByUsername("alice").getUsername());
    }

    @Test
    void displayNameIsUpperCase() {
        when(users.findById(1L)).thenReturn(Optional.of(new AppUser("alice", "a@example.com", "x", "CUSTOMER")));
        assertEquals("ALICE", service().displayName(1L));
    }

    @Test
    void staffRoleIsDetected() {
        assertTrue(service().isStaff(new AppUser("clerk", "c@example.com", "x", "STAFF")));
        assertFalse(service().isStaff(new AppUser("bob", "b@example.com", "x", "CUSTOMER")));
    }

    @Test
    void parsesAge() {
        assertEquals(42, service().parseAge(" 42 "));
        assertEquals(-1, service().parseAge("abc"));
    }

    @Test
    void reviewersOfUnreviewedBookIsNull() {
        when(reviews.findByBookId(77L)).thenReturn(List.of());
        assertNull(service().findReviewers(77L));
    }

    @Test
    void reviewersAreResolved() {
        when(reviews.findByBookId(1L)).thenReturn(List.of(new Review(1L, 5L, 4, "ok")));
        when(users.findById(5L)).thenReturn(Optional.of(new AppUser("eve", "e@example.com", "x", "CUSTOMER")));
        assertEquals(1, service().findReviewers(1L).size());
    }

    @Test
    void emailChangeForUnknownUserReturnsNull() {
        when(users.findById(404L)).thenReturn(Optional.empty());
        assertNull(service().changeEmail(404L, "new@example.com"));
    }

    @Test
    void emailChangeIsStored() {
        AppUser user = new AppUser("alice", "old@example.com", "x", "CUSTOMER");
        when(users.findById(1L)).thenReturn(Optional.of(user));
        when(users.save(any(AppUser.class))).thenAnswer(i -> i.getArgument(0));
        assertEquals("new@example.com", service().changeEmail(1L, "new@example.com").getEmail());
    }
}
