package com.jadli.bookstore.level1;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jadli.bookstore.domain.Book;
import java.util.List;
import org.junit.jupiter.api.Test;

public class BookFormatterTest {

    private final Book_Formatter formatter = new Book_Formatter();

    @Test
    public void formatsLine() {
        Book book = new Book("Dune", "Frank Herbert", "1", 9.99, 1, "scifi");
        assertEquals("Dune by Frank Herbert", formatter.Format_Line(book));
        assertEquals("Dune", formatter.lastFormattedTitle());
    }

    @Test
    public void joinsTitles() {
        List<Book> books = List.of(new Book("A", "x", "1", 1, 1, "g"), new Book("B", "x", "2", 1, 1, "g"));
        assertEquals("A, B", formatter.joinTitles(books));
    }
}
