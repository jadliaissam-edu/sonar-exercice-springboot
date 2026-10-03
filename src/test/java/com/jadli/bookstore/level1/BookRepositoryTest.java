package com.jadli.bookstore.level1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jadli.bookstore.domain.Book;
import com.jadli.bookstore.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    public void findsByTitleFragment() {
        bookRepository.save(new Book("Domain Driven Design", "Eric Evans", "9780321125217", 55.0, 4, "programming"));
        assertEquals(1, bookRepository.findByTitleContainingIgnoreCase("driven").size());
    }

    @Test
    public void findsByIsbn() {
        bookRepository.save(new Book("Test Driven Development", "Kent Beck", "9780321146533", 39.0, 2, "programming"));
        assertTrue(bookRepository.findByIsbn("9780321146533").isPresent());
    }

    @Test
    public void seedDataIsLoaded() {
        assertTrue(bookRepository.findByGenre("scifi").size() >= 2);
    }
}
