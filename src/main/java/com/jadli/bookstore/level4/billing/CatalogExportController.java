package com.jadli.bookstore.level4.billing;

import com.jadli.bookstore.domain.Book;
import com.jadli.bookstore.repository.BookRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/export")
public class CatalogExportController {

    private final BookRepository bookRepository;

    public CatalogExportController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping(value = "/catalog.csv", produces = "text/csv")
    public String exportCsv(@RequestParam(defaultValue = "all") String genre) {
        List<Book> books = genre.equals("all") ? bookRepository.findAll() : bookRepository.findByGenre(genre);
        StringBuilder csv = new StringBuilder();
        csv.append("id,title,author,isbn,price,stock\n");
        for (Book book : books) {
            String title = book.getTitle() == null ? "" : book.getTitle().replace("\"", "'");
            String author = book.getAuthor() == null ? "" : book.getAuthor().replace("\"", "'");
            String isbn = book.getIsbn() == null ? "" : book.getIsbn().trim();
            csv.append(book.getId()).append(',');
            csv.append('"').append(title).append('"').append(',');
            csv.append('"').append(author).append('"').append(',');
            csv.append(isbn).append(',');
            csv.append(book.getPrice()).append(',');
            csv.append(book.getStock()).append('\n');
        }
        return csv.toString();
    }
}
