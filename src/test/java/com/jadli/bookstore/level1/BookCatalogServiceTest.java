package com.jadli.bookstore.level1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jadli.bookstore.domain.Book;
import com.jadli.bookstore.repository.BookRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
public class BookCatalogServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookCatalogService service;

    private BookRequest request(String title, String isbn) {
        BookRequest request = new BookRequest();
        request.setTitle(title);
        request.setIsbn(isbn);
        request.setPrice(10);
        return request;
    }

    @Test
    public void findAllReturnsRequestedPage() {
        List<Book> books = List.of(new Book("A", "x", "1", 1, 1, "g"), new Book("B", "x", "2", 1, 1, "g"),
                new Book("C", "x", "3", 1, 1, "g"));
        when(bookRepository.findAll()).thenReturn(books);
        assertEquals(1, service.findAll(1, 2).size());
        assertEquals("C", service.findAll(1, 2).get(0).getTitle());
    }

    @Test
    public void searchReturnsMatches() {
        when(bookRepository.findByTitleContainingIgnoreCase("du")).thenReturn(List.of(new Book("Dune", "x", "1", 1, 1, "g")));
        assertEquals(1, service.search("du").size());
    }

    @Test
    public void searchWithoutMatchesIsEmpty() {
        when(bookRepository.findByTitleContainingIgnoreCase("zzz")).thenReturn(List.of());
        assertTrue(service.search("zzz").isEmpty());
    }

    @Test
    public void findByIdThrowsWhenMissing() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> service.findById(99L));
    }

    @Test
    public void createStoresNewBook() {
        when(bookRepository.findByIsbn("111")).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Book created = service.create(request("Dune", "111"));
        assertEquals("Dune", created.getTitle());
    }

    @Test
    public void createRejectsDuplicateIsbn() {
        when(bookRepository.findByIsbn("111")).thenReturn(Optional.of(new Book()));
        assertThrows(ResponseStatusException.class, () -> service.create(request("Dune", "111")));
    }

    @Test
    public void createRejectsLongTitle() {
        assertThrows(ResponseStatusException.class, () -> service.create(request("x".repeat(201), "222")));
    }

    @Test
    public void deleteRemovesExistingBook() {
        when(bookRepository.existsById(5L)).thenReturn(true);
        service.delete(5L);
        verify(bookRepository).deleteById(5L);
    }

    @Test
    public void deleteUnknownBookFails() {
        when(bookRepository.existsById(6L)).thenReturn(false);
        assertThrows(ResponseStatusException.class, () -> service.delete(6L));
    }
}
